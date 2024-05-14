package com.example.testing.controller;


import com.example.testing.model.Supplier;
import com.example.testing.service.SupplierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SupplierController.class)
class SupplierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private SupplierService supplierService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    void testAddSupplierWithAllFieldsEmpty() throws Exception {
        Supplier supplier = new Supplier(null, "", "", "", "");

        mockMvc.perform(post("/suppliers/add")
                        .flashAttr("supplier", supplier))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("supplier", "name", "contactPerson", "phone", "email"))
                .andExpect(view().name("add-supplier"));
    }

    @Test
    void testAddSupplierWithEmptyName() throws Exception {
        Supplier supplier = new Supplier(null, "", "Nguyen Tuan Minh", "033633289", "minh@gmail.com");

        mockMvc.perform(post("/suppliers/add")
                        .flashAttr("supplier", supplier))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("supplier", "name"))
                .andExpect(view().name("add-supplier"));
    }

    @Test
    void testAddSupplierWithNameExceedingMaxLength() throws Exception {
        String longName = "a".repeat(256);
        Supplier supplier = new Supplier(null, longName, "Nguyen Tuan Minh", "033633289", "minh@gmail.com");

        mockMvc.perform(post("/suppliers/add")
                        .flashAttr("supplier", supplier))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("supplier", "name"))
                .andExpect(view().name("add-supplier"));
    }

    @Test
    void testAddSupplierWithEmptyContactPerson() throws Exception {
        Supplier supplier = new Supplier(null, "Vinamilk", "", "033633289", "minh@gmail.com");

        mockMvc.perform(post("/suppliers/add")
                        .flashAttr("supplier", supplier))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("supplier", "contactPerson"))
                .andExpect(view().name("add-supplier"));
    }

    @Test
    void testAddSupplierWithContactPersonExceedingMaxLength() throws Exception {
        String longContactPerson = "a".repeat(256);
        Supplier supplier = new Supplier(null, "Vinamilk", longContactPerson, "033633289", "minh@gmail.com");

        mockMvc.perform(post("/suppliers/add")
                        .flashAttr("supplier", supplier))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("supplier", "contactPerson"))
                .andExpect(view().name("add-supplier"));
    }

    @Test
    void testAddSupplierWithEmptyPhone() throws Exception {
        Supplier supplier = new Supplier(null, "Vinamilk", "Nguyen Tuan Minh", "", "minh@gmail.com");

        mockMvc.perform(post("/suppliers/add")
                        .flashAttr("supplier", supplier))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("supplier", "phone"))
                .andExpect(view().name("add-supplier"));
    }

    @Test
    void testAddSupplierWithInvalidPhoneLength() throws Exception {
        Supplier supplier = new Supplier(null, "Vinamilk", "Nguyen Tuan Minh", "03363320000891", "minh@gmail.com");

        mockMvc.perform(post("/suppliers/add")
                        .flashAttr("supplier", supplier))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("supplier", "phone"))
                .andExpect(view().name("add-supplier"));
    }

    @Test
    void testAddSupplierWithInvalidPhoneCharacters() throws Exception {
        Supplier supplier = new Supplier(null, "Vinamilk", "Nguyen Tuan Minh", "abcdef", "minh@gmail.com");

        mockMvc.perform(post("/suppliers/add")
                        .flashAttr("supplier", supplier))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("supplier", "phone"))
                .andExpect(view().name("add-supplier"));
    }

    @Test
    void testAddSupplierWithEmptyEmail() throws Exception {
        Supplier supplier = new Supplier(null, "Vinamilk", "Nguyen Tuan Minh", "033633289", "");

        mockMvc.perform(post("/suppliers/add")
                        .flashAttr("supplier", supplier))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("supplier", "email"))
                .andExpect(view().name("add-supplier"));
    }

    @Test
    void testAddSupplierWithEmailExceedingMaxLength() throws Exception {
        String longEmail = "a".repeat(256) + "@example.com";
        Supplier supplier = new Supplier(null, "Vinamilk", "Nguyen Tuan Minh", "033633289", longEmail);

        mockMvc.perform(post("/suppliers/add")
                        .flashAttr("supplier", supplier))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("supplier", "email"))
                .andExpect(view().name("add-supplier"));
    }

    @Test
    void testAddSupplierWithInvalidEmailFormat() throws Exception {
        Supplier supplier = new Supplier(null, "Vinamilk", "Nguyen Tuan Minh", "033633289", "minhabc");

        mockMvc.perform(post("/suppliers/add")
                        .flashAttr("supplier", supplier))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("supplier", "email"))
                .andExpect(view().name("add-supplier"));
    }

    @Test
    void testAddSupplierWithDuplicateName() throws Exception {
        Supplier supplier = new Supplier(null, "KCC Computer", "Nguyen Tuan Minh", "0330633289", "minh@gmail.com");

        when(supplierService.isSupplierNameExist(anyString())).thenReturn(true);

        mockMvc.perform(post("/suppliers/add")
                        .flashAttr("supplier", supplier))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(view().name("add-supplier"));
    }

    @Test
    void testAddSupplierSuccessfully() throws Exception {
        Supplier supplier = new Supplier(null, "TH Corporation", "Nguyen Tuan Minh", "0336332089", "minh@gmail.com");

        when(supplierService.isSupplierNameExist(anyString())).thenReturn(false);

        mockMvc.perform(post("/suppliers/add")
                        .flashAttr("supplier", supplier))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("successMessage"))
                .andExpect(view().name("add-supplier-success"));
    }

    @Test
    void testShowSearchPage() throws Exception {
        mockMvc.perform(get("/suppliers/search-page"))
                .andExpect(status().isOk())
                .andExpect(view().name("search-suppliers"));
    }

    @Test
    void testSearchSuppliersByName() throws Exception {
        Supplier supplier = new Supplier(1L, "Supplier1", "Contact1", "1234567890", "supplier1@example.com");
        List<Supplier> suppliers = Collections.singletonList(supplier);

        when(supplierService.searchSupplierByName(anyString())).thenReturn(suppliers);

        mockMvc.perform(get("/suppliers/search")
                        .param("name", "Supplier1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("suppliers"))
                .andExpect(view().name("search-suppliers"));
    }

    @Test
    void testSearchSuppliersByName_NoResults() throws Exception {
        when(supplierService.searchSupplierByName(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/suppliers/search")
                        .param("name", "NonExistingSupplier"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("suppliers"))
                .andExpect(model().attribute("suppliers", Collections.emptyList()))
                .andExpect(view().name("search-suppliers"));
    }
}
