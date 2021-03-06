package se.ericsson.cf.scott.sandbox.whc.xtra.planning

import eu.scott.warehouse.domains.pddl.Problem
import eu.scott.warehouse.lib.InstanceWithResources
import eu.scott.warehouse.lib.OslcHelper
import eu.scott.warehouse.lib.link
import org.eclipse.lyo.oslc4j.core.model.IExtendedResource
import org.eclipse.lyo.oslc4j.core.model.Link
import org.eclipse.lyo.oslc4j.provider.jena.JenaModelHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI
import java.util.LinkedList

interface Label {
    val value: String
}

data class RobotLabel(override val value: String) : Label
data class ShelfLabel(override val value: String) : Label
data class BeltLabel(override val value: String) : Label
data class BoxLabel(override val value: String) : Label


class ProblemBuilder(val oslcHelper: OslcHelper, private val planRequestHelper: PlanRequestHelper) {

    companion object {
        val log: Logger = LoggerFactory.getLogger(ProblemBuilder::class.java)
    }

    private var width: Int = -1
    private var height: Int = -1

    // TODO Andrew@2019-02-19: use typed labels
    private val robots: MutableSet<String> = HashSet()

    // TODO Andrew@2019-02-19: use typed labels
    // |TODO Andrew@2019-02-19: use se.ericsson.cf.scott.sandbox.whc.xtra.planning.PlanRequestHelper.freeRobot
    private val freeRobots: MutableSet<String> = HashSet()

    private val initPositions: MutableMap<Pair<Int, Int>, Label> = HashMap()
    private val goalPositions: MutableMap<Pair<Int, Int>, Label> = HashMap()

    private val boxOnShelfInit: MutableSet<Pair<BoxLabel, ShelfLabel>> = HashSet()
    private val boxOnBeltGoal: MutableSet<Pair<BoxLabel, BeltLabel>> = HashSet()

    private val resources: MutableSet<IExtendedResource> = LinkedHashSet()

    private var problem: Problem = Problem(oslcHelper.u("scott-warehouse-problem"))


    fun build(): ProblemRequestState {
        if (width == 0 || height == 0) {
            throw IllegalArgumentException("Width and height must be set")
        }

        val resources: MutableSet<IExtendedResource> = resources

        problem.label = "scott-warehouse-problem"
        problem.domain = Link(oslcHelper.u("scott-warehouse"))

        resources.add(problem)

        buildObjects()
        buildInitState()
        buildGoalState()
        buildMinimisationFn()

        val model = JenaModelHelper.createJenaModel(resources.toTypedArray())
        return ProblemRequestState(model)
    }

    fun problemUri(uri: URI): ProblemBuilder {
        problem.about = uri
        return this
    }

    fun genLabel(s: String): ProblemBuilder {
        problem.label = s
        return this
    }

    fun problemDomain(link: Link): ProblemBuilder {
        problem.domain = link
        return this
    }

    private fun buildMinimisationFn() {
        val minFn = planRequestHelper.minFn()
        problem.minimize = minFn.link
        resources += minFn
    }

    private fun buildObjects() {
        // Shop floor

        for(x in 1..width) {
            for(y in 1..height) {
                val waypoint: IExtendedResource = planRequestHelper.waypoint(x, y)
                addObject(waypoint)
            }
        }

        // Stationary objects
        initPositions.forEach { coord, label ->
            when (label) {
                is ShelfLabel -> addObject(planRequestHelper.shelf(label.value))
                is BeltLabel  -> addObject(planRequestHelper.belt(label.value))
            }
        }

        // Robots
        robots.forEach { label ->
            val robot = planRequestHelper.robot(label)
            addObject(robot)
        }

        boxOnShelfInit.forEach { pair: Pair<BoxLabel, ShelfLabel> ->
            val box = planRequestHelper.box(pair.first.value)
            addObject(box)
        }
    }


    private fun buildInitState() {
        freeRobots.forEach { label: String ->
            val freeRobot = planRequestHelper.freeRobot(lookupRobot(label))
            addInit(freeRobot)
        }

        for (x in 1..width) {
            for (y in 1..height) {
                if (x < width) {
                    addInit(
                        planRequestHelper.canMove(lookupWaypoint(x, y), lookupWaypoint(x + 1, y)))
                }
                if (x > 1) {
                    addInit(
                        planRequestHelper.canMove(lookupWaypoint(x, y), lookupWaypoint(x - 1, y)))
                }
                if (y < height) {
                    addInit(
                        planRequestHelper.canMove(lookupWaypoint(x, y), lookupWaypoint(x, y + 1)))
                }
                if (y > 1) {
                    addInit(
                        planRequestHelper.canMove(lookupWaypoint(x, y), lookupWaypoint(x, y - 1)))
                }
            }
        }

        initPositions.forEach { coord, label ->
            val wp = lookupWaypoint(coord)
            val objAtXY: InstanceWithResources<IExtendedResource> = when (label) {
                is ShelfLabel -> planRequestHelper.shelfAt(lookupShelf(label.value), wp)
                is BeltLabel  -> planRequestHelper.beltAt(lookupBelt(label.value), wp)
                is RobotLabel -> planRequestHelper.robotAt(lookupRobot(label.value), wp)
                else          -> throw IllegalStateException("Unexpected label type")
            }
            addInit(objAtXY)
        }

        boxOnShelfInit.forEach { pair: Pair<BoxLabel, ShelfLabel> ->
            val box = lookupBox(pair.first.value)
            val shelf = lookupShelf(pair.second.value)
            val onShelf = planRequestHelper.onShelf(box, shelf)
            addInit(onShelf)
        }
    }

    private fun buildGoalState() {
        //
        //        // GOAL STATE
        //
        //        val goal = and(onBelt(b1, cb1))
        //        problem.goal = goal.instance.link
        //        resources += goal.resources

        val goalPredicates: MutableList<InstanceWithResources<IExtendedResource>> = LinkedList()

        val onBeltPredicates = boxOnBeltGoal.map { pair: Pair<BoxLabel, BeltLabel> ->
            planRequestHelper.onBelt(lookupBox(pair.first.value), lookupBelt(pair.second.value))
        }
        goalPredicates.addAll(onBeltPredicates)


        goalPositions.forEach { coord, label ->
            when (label) {
                is RobotLabel -> {
                    val robot = lookupRobot(label.value)
//                    val x = lookupCoordX(coord.first)
//                    val y = lookupCoordY(coord.second)
                    val wp = lookupWaypoint(coord)
                    val robotAt = planRequestHelper.robotAt(robot, wp)

                    goalPredicates.add(robotAt)
                }
            }
        }


        // just a conjunction of all terms
        val andGoal = planRequestHelper.and(goalPredicates)

        // no need for an addGoal() function
        problem.goal = andGoal.instance.link
        resources += andGoal.resources
    }

    private fun addObject(resource: IExtendedResource) {
        problem.pddlObject.add(resource.link)
        resources += resource
    }


    private fun addObject(complexResource: InstanceWithResources<IExtendedResource>) {
        problem.pddlObject.add(complexResource.instance.link)

        resources += complexResource.instance
        resources += complexResource.resources
    }

    private fun addInit(resource: IExtendedResource) {
        problem.init.add(resource.link)
        resources += resource
    }


    private fun addInit(complexResource: InstanceWithResources<IExtendedResource>) {
        problem.init.add(complexResource.instance.link)

        resources += complexResource.instance
        resources += complexResource.resources
    }

    private fun lookupWaypoint(c: Pair<Int, Int>): IExtendedResource {
        return lookupNaive("x${c.first}y${c.second}")
    }

    private fun lookupWaypoint(x: Int, y: Int): IExtendedResource = lookupWaypoint(Pair(x,y))


    private fun lookupShelf(label: String): IExtendedResource {
        return lookupNaive(label)
    }

    private fun lookupBelt(label: String): IExtendedResource {
        return lookupNaive(label)
    }

    private fun lookupRobot(label: String): IExtendedResource {
        return lookupNaive(label)
    }

    private fun lookupBox(label: String): IExtendedResource {
        return lookupNaive(label)
    }

    private fun lookupNaive(label: String): IExtendedResource {
        val matchingResources: List<IExtendedResource> = resources.filter { r ->
            r.about == oslcHelper.u(label)
        }
        val size = matchingResources.size
        if (size > 0) {
            if (size > 1) {
                log.warn("More than one resource matches label '$label'")
            }
            return matchingResources.single()
        } else {
            log.error("No match for resource '$label'")
            if(log.isDebugEnabled) {
                val resourceList = resources.map { it.toString() }
                    .joinToString("\n")
                log.debug("Registered resources: \n$resourceList")
            }
            throw IllegalArgumentException("Resource '$label' is not registered in the problem")
        }
    }

    fun warehouseSize(_width: Int, _height: Int): ProblemBuilder {
        if (_width <= 0 || _height <= 0) {
            throw IllegalArgumentException("Width & height must be within (0, INT_MAX]")
        }
        this.width = _width
        this.height = _height
        return this
    }

    fun robotsActive(labels: Collection<String>): ProblemBuilder {
        robots.addAll(labels)
        freeRobots.addAll(labels)
        return this
    }

    fun robotsInactive(labels: Collection<String>): ProblemBuilder {
        if (freeRobots.intersect(labels).isNotEmpty()) {
            throw IllegalArgumentException("Active & inactive robots shall not overlap")
        }
        robots.addAll(labels)
        return this
    }

    fun robotAtInit(label: String, x: Int, y: Int): ProblemBuilder {
        if (initPositions.putIfAbsent(Pair(x, y), RobotLabel(label)) != null) {
            throw IllegalArgumentException("Waypoint ($x, $y) is already occupied")
        }
        return this
    }

    fun robotAtGoal(label: String, x: Int, y: Int): ProblemBuilder {
        if (goalPositions.putIfAbsent(Pair(x, y), RobotLabel(label)) != null) {
            throw IllegalArgumentException(
                "Waypoint ($x, $y) is already planned to be occupied (goal)")
        }
        return this
    }

    fun shelfAt(label: String, x: Int, y: Int): ProblemBuilder {
        if (initPositions.putIfAbsent(Pair(x, y), ShelfLabel(label)) != null) {
            throw IllegalArgumentException("Waypoint ($x, $y) is already occupied")
        }
        return this
    }

    fun beltAt(label: String, x: Int, y: Int): ProblemBuilder {
        if (initPositions.putIfAbsent(Pair(x, y), BeltLabel(label)) != null) {
            throw IllegalArgumentException("Waypoint ($x, $y) is already occupied")
        }
        return this
    }

    fun boxOnShelfInit(boxLabel: String, shelfLabel: String): ProblemBuilder {
        boxOnShelfInit.add(Pair(BoxLabel(boxLabel), ShelfLabel(shelfLabel)))
        return this
    }

    fun boxOnBeltGoal(boxLabel: String, beltLabel: String): ProblemBuilder {
        boxOnBeltGoal.add(Pair(BoxLabel(boxLabel), BeltLabel(beltLabel)))
        return this
    }

}
