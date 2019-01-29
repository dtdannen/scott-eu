// Start of user code Copyright
/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *
 *     Michael Fiedler     - initial API and implementation for Bugzilla adapter
 *     Jad El-khoury       - initial implementation of code generator (https://bugs.eclipse.org/bugs/show_bug.cgi?id=422448)
 *     Jim Amsden          - Support for UI Preview (494303)
 *
 * This file is generated by org.eclipse.lyo.oslc4j.codegenerator
 *******************************************************************************/
// End of user code

package se.ericsson.cf.scott.sandbox.whc.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.UriBuilder;

import org.apache.wink.json4j.JSONObject;
import org.eclipse.lyo.oslc4j.provider.json4j.JsonHelper;
import org.eclipse.lyo.oslc4j.core.OSLC4JUtils;
import org.eclipse.lyo.oslc4j.core.annotation.OslcCreationFactory;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDialog;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDialogs;
import org.eclipse.lyo.oslc4j.core.annotation.OslcQueryCapability;
import org.eclipse.lyo.oslc4j.core.annotation.OslcService;
import org.eclipse.lyo.oslc4j.core.model.Compact;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;
import org.eclipse.lyo.oslc4j.core.model.Preview;
import org.eclipse.lyo.oslc4j.core.model.ServiceProvider;
import org.eclipse.lyo.oslc4j.core.model.Link;
import org.eclipse.lyo.oslc4j.core.model.AbstractResource;

import se.ericsson.cf.scott.sandbox.whc.WarehouseControllerManager;
import se.ericsson.cf.scott.sandbox.whc.WarehouseControllerConstants;
import eu.scott.warehouse.domains.mission.MissionDomainConstants;
import eu.scott.warehouse.domains.mission.MissionDomainConstants;
import se.ericsson.cf.scott.sandbox.whc.servlet.ServiceProviderCatalogSingleton;
import eu.scott.warehouse.domains.mission.AgentRequest;
import eu.scott.warehouse.domains.mission.RegistrationRequest;

// Start of user code imports
// End of user code

// Start of user code pre_class_code
// End of user code
@OslcService(MissionDomainConstants.MISSIONONTOLOGY_DOMAIN)
@Path("service2/registrationRequests")
public class ServiceProviderService2
{
    @Context private HttpServletRequest httpServletRequest;
    @Context private HttpServletResponse httpServletResponse;
    @Context private UriInfo uriInfo;

    // Start of user code class_attributes
    // End of user code

    // Start of user code class_methods
    // End of user code

    public ServiceProviderService2()
    {
        super();
    }

    /**
     * Create a single RegistrationRequest via RDF/XML, XML or JSON POST
     *
     */
    @OslcCreationFactory
    (
         title = "RegistrationCF",
         label = "Registration Creation Factory",
         resourceShapes = {OslcConstants.PATH_RESOURCE_SHAPES + "/" + MissionDomainConstants.REGISTRATIONREQUEST_PATH},
         resourceTypes = {MissionDomainConstants.REGISTRATIONREQUEST_TYPE},
         usages = {}
    )
    @POST
    @Path("register")
    @Consumes({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON, OslcMediaType.TEXT_TURTLE})
    @Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON, OslcMediaType.TEXT_TURTLE})
    public Response createRegistrationRequest(
            
            final RegistrationRequest aResource
        ) throws IOException, ServletException
    {
        try {
            RegistrationRequest newResource = WarehouseControllerManager.createRegistrationRequest(httpServletRequest, aResource);
            httpServletResponse.setHeader("ETag", WarehouseControllerManager.getETagFromRegistrationRequest(newResource));
            return Response.created(newResource.getAbout()).entity(newResource).header(WarehouseControllerConstants.HDR_OSLC_VERSION, WarehouseControllerConstants.OSLC_VERSION_V2).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new WebApplicationException(e);
        }
    }

}
