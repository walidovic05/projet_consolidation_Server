package com.projet_consolidation.projet_consolidation.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projet_consolidation.projet_consolidation.business.UtilisateurService;
import com.projet_consolidation.projet_consolidation.infrastructure.Utilisateur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * A test class for the user rest API
 *
 * @author walid BIZID
 * @version 1
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        MockMvc.class
})
public class UtilisateurControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UtilisateurService utilisateurService;

    Utilisateur utilisateur;

    /**
     * Should instantiate a new user before each test
     */
    @BeforeEach
    public void setUp() {
        utilisateur = new Utilisateur( (long)2004 ,"Mohamed", "bizid", "Mohamed.bizid@hotmail.com", LocalDate.of(2009,04,26));
    }

    /**
     * Should return all the users by page
     *
     * @throws Exception
     */
    @Test
    public void shouldGetAllUsers() throws Exception {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        utilisateurs.add(new Utilisateur("walid", "bizid", "walid.bizid@hotmail.com", LocalDate.of(1994,05,29)));
        utilisateurs.add(new Utilisateur("Majdi", "bizid", "majdi.bizid@hotmail.com", LocalDate.of(1997,05,25)));
        Page<Utilisateur> pagedUsers = new PageImpl(utilisateurs);
        Pageable pageRequest = PageRequest.of(0, 4);
        when(utilisateurService.getAllUsers(pageRequest)).thenReturn(pagedUsers);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andDo(print());
    }

    /**
     * Should create a new user successfully
     *
     * @throws Exception
     */
    @Test
    public void shouldCreateUserSuccessfully() throws Exception {
        when(utilisateurService.saveUser(any(Utilisateur.class))).thenReturn(utilisateur);

        ObjectMapper objectMapper = new ObjectMapper();
        String utilisateurJSON = objectMapper.writeValueAsString(utilisateur);

        ResultActions result = mockMvc.perform(post("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(utilisateurJSON)
        );

        result.andExpect(status().isCreated());
    }

    /**
     * Should delete a specific user successfully by his id
     *
     * @throws Exception
     */
    @Test
    public void shouldDeleteUserSuccessfully() throws Exception {
        when(utilisateurService.saveUser(any(Utilisateur.class))).thenReturn(utilisateur);
        doNothing().when(utilisateurService).deleteUser(utilisateur.getId());
        mockMvc.perform(delete("/api/v1/user/{userId}",1351))
                .andExpect(content().string("user deleted successfully"));
    }

    @Test
    public void shouldGetErrorUserNotExistBeforeDelete() throws Exception {
        mockMvc.perform(delete("/api/v1/user/{userId}",1351))
                .andExpect(status().isNotFound());
    }

    /**
     * Should successfully get a specific user by his id
     *
     * @throws Exception
     */
    @Test
    public void shouldGetuserByIdSuccessfully() throws Exception {
        when(utilisateurService.getUserById(utilisateur.getId())).thenReturn(Optional.ofNullable(utilisateur));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/{userId}",2004))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("bizid"))
                .andExpect(jsonPath("$.prenom").value("Mohamed"))
                .andDo(print());
    }

    /**
     * Should get an not found error when a user dosen't exist
     *
     * @throws Exception
     */
    @Test
    public void shouldGetErrorUserNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/users/{userId}", 2000))
                .andExpect(status().isNotFound());
    }

    /**
     * Should get an invalid parameter error ( invalid id )
     *
     * @throws Exception
     */
    @Test
    public void shouldGetErrorNoValidId() throws Exception {
        mockMvc.perform(get("/api/v1/users/{userId}", 2+"000a"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Should update a specific user successfully
     *
     * @throws Exception
     */
    @Test
    public void shouldUpdateUserSuccessfully() throws Exception {
        Utilisateur updatedUser = new Utilisateur("Said", "Bizid", "said.bizid@gmail.com", LocalDate.of(1994,05,29));

        ObjectMapper objectMapper = new ObjectMapper();
        String utilisateurJSON = objectMapper.writeValueAsString(updatedUser);

        mockMvc.perform(put("/api/v1/user/{userId}", 2004)
                .contentType(MediaType.APPLICATION_JSON)
                .content(utilisateurJSON)
        ).andExpect(status().isOk());
    }

    /**
     * Should get an error user with entered id not exist
     *
     * @throws Exception
     */
    @Test
    void shouldGetErrorUserNotExistBeforeUpdate() throws Exception {
        Utilisateur updatedUser = new Utilisateur("Said", "Bizid", "said.bizid@gmail.com", LocalDate.of(1994,05,29));

        ObjectMapper objectMapper = new ObjectMapper();
        String utilisateurJSON = objectMapper.writeValueAsString(updatedUser);

        mockMvc.perform(put("/api/v1/user/{userId}", 2000)
                .contentType(MediaType.APPLICATION_JSON)
                .content(utilisateurJSON)
        ).andExpect(status().isNotFound());
    }
}
