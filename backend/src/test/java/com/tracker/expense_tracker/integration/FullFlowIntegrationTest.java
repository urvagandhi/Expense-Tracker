package com.tracker.expense_tracker.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Full end-to-end integration test covering the complete user flow:
 * Register -> Login -> CRUD Expenses -> CRUD Budgets -> Budget auto-sync -> User profile -> Cleanup
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FullFlowIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private static String jwtToken;
    private static Long userId;
    private static Long expenseId;
    private static Long expense2Id;
    private static Long budgetId;

    // ==================== AUTH ====================

    @Test
    @Order(1)
    @DisplayName("1. Register a new user")
    void register() throws Exception {
        String body = """
                {"username":"Urva Gandhi","email":"urva@example.com","password":"securePass123"}
                """;

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.email").value("urva@example.com"))
                .andExpect(jsonPath("$.data.username").value("Urva Gandhi"))
                .andExpect(jsonPath("$.data.role").value("USER"))
                .andExpect(jsonPath("$.data.type").value("Bearer"))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        jwtToken = json.get("data").get("token").asText();
        userId = json.get("data").get("userId").asLong();

        assertThat(jwtToken).isNotBlank();
        assertThat(userId).isPositive();
    }

    @Test
    @Order(2)
    @DisplayName("2. Reject duplicate registration")
    void registerDuplicate() throws Exception {
        String body = """
                {"username":"Another","email":"urva@example.com","password":"password123"}
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("already exists")));
    }

    @Test
    @Order(3)
    @DisplayName("3. Reject registration with invalid data")
    void registerValidationError() throws Exception {
        String body = """
                {"username":"","email":"not-an-email","password":"12"}
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data.email").isNotEmpty())
                .andExpect(jsonPath("$.data.username").isNotEmpty())
                .andExpect(jsonPath("$.data.password").isNotEmpty());
    }

    @Test
    @Order(4)
    @DisplayName("4. Login with correct credentials")
    void login() throws Exception {
        String body = """
                {"email":"urva@example.com","password":"securePass123"}
                """;

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andReturn();

        // Use the login token going forward
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        jwtToken = json.get("data").get("token").asText();
    }

    @Test
    @Order(5)
    @DisplayName("5. Reject login with wrong password")
    void loginWrongPassword() throws Exception {
        String body = """
                {"email":"urva@example.com","password":"wrongPassword"}
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(6)
    @DisplayName("6. Reject access without token")
    void accessWithoutToken() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isForbidden());
    }

    // ==================== USER PROFILE ====================

    @Test
    @Order(10)
    @DisplayName("10. Get current user profile")
    void getCurrentUser() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("urva@example.com"))
                .andExpect(jsonPath("$.data.username").value("Urva Gandhi"))
                .andExpect(jsonPath("$.data.role").value("USER"));
    }

    @Test
    @Order(11)
    @DisplayName("11. Get user by ID")
    void getUserById() throws Exception {
        mockMvc.perform(get("/api/users/" + userId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(userId));
    }

    @Test
    @Order(12)
    @DisplayName("12. Get all users")
    void getAllUsers() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)));
    }

    @Test
    @Order(13)
    @DisplayName("13. Update user profile")
    void updateUser() throws Exception {
        String body = """
                {"username":"Urva G"}
                """;

        mockMvc.perform(put("/api/users/" + userId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("Urva G"));
    }

    @Test
    @Order(14)
    @DisplayName("14. Get non-existent user returns 404")
    void getUserNotFound() throws Exception {
        mockMvc.perform(get("/api/users/99999")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ==================== EXPENSES ====================

    @Test
    @Order(20)
    @DisplayName("20. Add first expense")
    void addExpense1() throws Exception {
        String body = """
                {"amount":45.50,"category":"Food","description":"Lunch at cafe","expenseDate":"2025-12-15"}
                """;

        MvcResult result = mockMvc.perform(post("/api/expenses")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.amount").value(45.50))
                .andExpect(jsonPath("$.data.category").value("Food"))
                .andExpect(jsonPath("$.data.description").value("Lunch at cafe"))
                .andExpect(jsonPath("$.data.expenseDate").value("2025-12-15"))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        expenseId = json.get("data").get("id").asLong();
    }

    @Test
    @Order(21)
    @DisplayName("21. Add second expense")
    void addExpense2() throws Exception {
        String body = """
                {"amount":120.00,"category":"Transport","description":"Uber rides","expenseDate":"2025-12-18"}
                """;

        MvcResult result = mockMvc.perform(post("/api/expenses")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.amount").value(120.00))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        expense2Id = json.get("data").get("id").asLong();
    }

    @Test
    @Order(22)
    @DisplayName("22. Reject expense with invalid data")
    void addExpenseValidationError() throws Exception {
        String body = """
                {"amount":-10,"category":"","description":"","expenseDate":null}
                """;

        mockMvc.perform(post("/api/expenses")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @Order(23)
    @DisplayName("23. Get all expenses (paginated)")
    void getExpensesPaginated() throws Exception {
        mockMvc.perform(get("/api/expenses")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.totalElements").value(2));
    }

    @Test
    @Order(24)
    @DisplayName("24. Get all expenses (no pagination)")
    void getAllExpenses() throws Exception {
        mockMvc.perform(get("/api/expenses/all")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @Order(25)
    @DisplayName("25. Update expense")
    void updateExpense() throws Exception {
        String body = """
                {"amount":55.00,"category":"Food","description":"Dinner at restaurant","expenseDate":"2025-12-15"}
                """;

        mockMvc.perform(put("/api/expenses/" + expenseId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.amount").value(55.00))
                .andExpect(jsonPath("$.data.description").value("Dinner at restaurant"));
    }

    @Test
    @Order(26)
    @DisplayName("26. Update non-existent expense returns 404")
    void updateExpenseNotFound() throws Exception {
        String body = """
                {"amount":10,"category":"Test","description":"Test","expenseDate":"2025-12-01"}
                """;

        mockMvc.perform(put("/api/expenses/99999")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    // ==================== BUDGETS ====================

    @Test
    @Order(30)
    @DisplayName("30. Create budget for December 2025")
    void createBudget() throws Exception {
        String body = """
                {"month":12,"year":2025,"budgetLimit":5000.00}
                """;

        MvcResult result = mockMvc.perform(post("/api/budgets")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.budgetLimit").value(5000.00))
                .andExpect(jsonPath("$.data.month").value(12))
                .andExpect(jsonPath("$.data.year").value(2025))
                .andExpect(jsonPath("$.data.totalExpense").value(175.00))  // 55 + 120 = 175
                .andExpect(jsonPath("$.data.remainingBudget").value(4825.00))  // 5000 - 175
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        budgetId = json.get("data").get("id").asLong();
    }

    @Test
    @Order(31)
    @DisplayName("31. Reject duplicate budget for same month/year")
    void createDuplicateBudget() throws Exception {
        String body = """
                {"month":12,"year":2025,"budgetLimit":3000.00}
                """;

        mockMvc.perform(post("/api/budgets")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @Order(32)
    @DisplayName("32. Reject budget with invalid data")
    void createBudgetValidationError() throws Exception {
        String body = """
                {"month":13,"year":1999,"budgetLimit":-100}
                """;

        mockMvc.perform(post("/api/budgets")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(33)
    @DisplayName("33. Get budget by month/year")
    void getBudgetByMonthYear() throws Exception {
        mockMvc.perform(get("/api/budgets/12/2025")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.budgetLimit").value(5000.00))
                .andExpect(jsonPath("$.data.totalExpense").value(175.00));
    }

    @Test
    @Order(34)
    @DisplayName("34. Get all budgets")
    void getAllBudgets() throws Exception {
        mockMvc.perform(get("/api/budgets")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    @Order(35)
    @DisplayName("35. Get non-existent budget returns 404")
    void getBudgetNotFound() throws Exception {
        mockMvc.perform(get("/api/budgets/1/2030")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    // ==================== BUDGET AUTO-SYNC ====================

    @Test
    @Order(40)
    @DisplayName("40. Add expense and verify budget auto-updates")
    void budgetAutoSyncOnAddExpense() throws Exception {
        String body = """
                {"amount":200.00,"category":"Shopping","description":"Clothes","expenseDate":"2025-12-20"}
                """;

        mockMvc.perform(post("/api/expenses")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        // Budget should now reflect 55 + 120 + 200 = 375
        mockMvc.perform(get("/api/budgets/12/2025")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(jsonPath("$.data.totalExpense").value(375.00))
                .andExpect(jsonPath("$.data.remainingBudget").value(4625.00));
    }

    @Test
    @Order(41)
    @DisplayName("41. Delete expense and verify budget auto-updates")
    void budgetAutoSyncOnDeleteExpense() throws Exception {
        // Delete the second expense (120.00)
        mockMvc.perform(delete("/api/expenses/" + expense2Id)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Budget should now reflect 55 + 200 = 255
        mockMvc.perform(get("/api/budgets/12/2025")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(jsonPath("$.data.totalExpense").value(255.00))
                .andExpect(jsonPath("$.data.remainingBudget").value(4745.00));
    }

    @Test
    @Order(42)
    @DisplayName("42. Verify expense was actually deleted")
    void verifyExpenseDeleted() throws Exception {
        mockMvc.perform(get("/api/expenses/all")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));  // 3 added, 1 deleted = 2
    }

    // ==================== UPDATE BUDGET ====================

    @Test
    @Order(50)
    @DisplayName("50. Update budget limit")
    void updateBudget() throws Exception {
        String body = """
                {"month":12,"year":2025,"budgetLimit":3000.00}
                """;

        mockMvc.perform(put("/api/budgets/" + budgetId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.budgetLimit").value(3000.00));
    }

    // ==================== MULTI-USER ISOLATION ====================

    @Test
    @Order(60)
    @DisplayName("60. Register second user")
    void registerSecondUser() throws Exception {
        String body = """
                {"username":"Other User","email":"other@example.com","password":"password456"}
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(61)
    @DisplayName("61. Second user cannot see first user's expenses")
    void secondUserCannotSeeOtherExpenses() throws Exception {
        // Login as second user
        String loginBody = """
                {"email":"other@example.com","password":"password456"}
                """;
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String otherToken = json.get("data").get("token").asText();

        // Second user should have 0 expenses
        mockMvc.perform(get("/api/expenses/all")
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));

        // Second user should have 0 budgets
        mockMvc.perform(get("/api/budgets")
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    @Order(62)
    @DisplayName("62. Second user cannot delete first user's expense")
    void secondUserCannotDeleteOtherExpense() throws Exception {
        String loginBody = """
                {"email":"other@example.com","password":"password456"}
                """;
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String otherToken = json.get("data").get("token").asText();

        // Try to delete first user's expense — should get 404 (not found for this user)
        mockMvc.perform(delete("/api/expenses/" + expenseId)
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isNotFound());
    }

    // ==================== CLEANUP ====================

    @Test
    @Order(70)
    @DisplayName("70. Delete budget")
    void deleteBudget() throws Exception {
        mockMvc.perform(delete("/api/budgets/" + budgetId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Verify deleted
        mockMvc.perform(get("/api/budgets/12/2025")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(71)
    @DisplayName("71. Delete remaining expense")
    void deleteRemainingExpense() throws Exception {
        mockMvc.perform(delete("/api/expenses/" + expenseId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    @Order(72)
    @DisplayName("72. Verify all expenses cleaned up (only 1 left - the shopping one)")
    void verifyCleanup() throws Exception {
        mockMvc.perform(get("/api/expenses/all")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }
}
