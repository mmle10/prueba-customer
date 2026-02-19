package com.example.customer.service;

import com.example.customer.service.CountryResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@ApplicationScoped
@RegisterRestClient(configKey = "restcountries-api")
public interface CountryService {
    
    @GET
    @Path("/alpha/{code}")
    List<CountryResponse> getCountryByCode(@PathParam("code") String code);
    
    @GET
    @Path("/name/{name}")
    List<CountryResponse> getCountryByName(@PathParam("name") String name);
}
