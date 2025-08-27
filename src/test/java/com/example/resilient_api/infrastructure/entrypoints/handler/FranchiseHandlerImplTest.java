package com.example.resilient_api.infrastructure.entrypoints.handler;

import com.example.resilient_api.domain.model.Franchise;
import com.example.resilient_api.domain.usecase.AddBranchToFranchiseUseCase;
import com.example.resilient_api.domain.usecase.CreateFranchiseUseCase;
import com.example.resilient_api.domain.usecase.GetMaxStockPerBranchUseCase;
import com.example.resilient_api.domain.usecase.UpdateFranchiseNameUseCase;
import com.example.resilient_api.infrastructure.entrypoints.dto.Branch;
import com.example.resilient_api.infrastructure.entrypoints.dto.FranchiseMaxStockDTO;
import com.example.resilient_api.infrastructure.entrypoints.dto.UpdateFranchiseNameRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunctions;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class FranchiseHandlerImplTest {

    private static final String BASE_PATH = "/franchises";
    private static final String FRANCHISE_ID = "FRA-001";
    private static final String NEW_NAME = "New Franchise";
    private static final String BRANCH_ID = "SUC-123";
    private static final String ERROR_MESSAGE = "Error";

    @Mock
    private CreateFranchiseUseCase createFranchiseUseCase;
    @Mock
    private AddBranchToFranchiseUseCase addBranchToFranchiseUseCase;
    @Mock
    private GetMaxStockPerBranchUseCase getMaxStockPerBranchUseCase;
    @Mock
    private UpdateFranchiseNameUseCase updateFranchiseNameUseCase;

    @InjectMocks
    private FranchiseHandlerImpl handler;

    private WebTestClient client;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        client = WebTestClient.bindToRouterFunction(
                RouterFunctions.route()
                        .POST(BASE_PATH, handler::createFranchise)
                        .POST(BASE_PATH + "/{" + "id" + "}/branchs", handler::addBranchToFranchise)
                        .GET(BASE_PATH + "/{" + "id" + "}/max-stock", handler::getMaxStockPerBranch)
                        .PATCH(BASE_PATH + "/{" + "id" + "}", handler::updateFranchiseName)
                        .build()
        ).build();
    }

    @Test
    void shouldCreateFranchiseSuccessfully() {
        Franchise franchise = Franchise.builder().id(FRANCHISE_ID).nombre(NEW_NAME).build();
        when(createFranchiseUseCase.execute(any())).thenReturn(Mono.just(franchise));

        client.post().uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(franchise)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void shouldReturnErrorWhenCreateFranchiseFails() {
        when(createFranchiseUseCase.execute(any())).thenReturn(Mono.error(new RuntimeException(ERROR_MESSAGE)));

        client.post().uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new Franchise())
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void shouldAddBranchSuccessfully() {
        Franchise franchise = Franchise.builder().id(FRANCHISE_ID).build();
        when(addBranchToFranchiseUseCase.execute(eq(FRANCHISE_ID), eq(BRANCH_ID))).thenReturn(Mono.just(franchise));

        Branch request = new Branch();
        request.setBranchId(BRANCH_ID);

        client.post().uri(BASE_PATH + "/" + FRANCHISE_ID + "/branchs")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturnBadRequestWhenBranchIdIsMissing() {
        Branch request = new Branch();

        client.post().uri(BASE_PATH + "/" + FRANCHISE_ID + "/branchs")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldGetMaxStockPerBranchSuccessfully() {
        FranchiseMaxStockDTO dto = FranchiseMaxStockDTO.builder().id(FRANCHISE_ID).build();
        when(getMaxStockPerBranchUseCase.execute(eq(FRANCHISE_ID))).thenReturn(Mono.just(dto));

        client.get().uri(BASE_PATH + "/" + FRANCHISE_ID + "/max-stock")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturnNotFoundWhenMaxStockIsEmpty() {
        when(getMaxStockPerBranchUseCase.execute(eq(FRANCHISE_ID))).thenReturn(Mono.empty());

        client.get().uri(BASE_PATH + "/" + FRANCHISE_ID + "/max-stock")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldUpdateFranchiseNameSuccessfully() {
        Franchise franchise = Franchise.builder().id(FRANCHISE_ID).nombre(NEW_NAME).build();
        when(updateFranchiseNameUseCase.execute(eq(FRANCHISE_ID), eq(NEW_NAME))).thenReturn(Mono.just(franchise));

        UpdateFranchiseNameRequest request = new UpdateFranchiseNameRequest();
        request.setName(NEW_NAME);

        client.patch().uri(BASE_PATH + "/" + FRANCHISE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturnBadRequestWhenFranchiseNameIsMissing() {
        UpdateFranchiseNameRequest request = new UpdateFranchiseNameRequest();

        client.patch().uri(BASE_PATH + "/" + FRANCHISE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }
}
