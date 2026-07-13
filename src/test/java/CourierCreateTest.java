import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.example.pojo.CourierCreateRequest;
import org.example.pojo.CourierLoginRequest;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.CoreMatchers.equalTo;

public class CourierCreateTest {

    private String login;
    private final String password = "qwerty123";
    private final String firstName = "Игорь";

    private CourierSteps courierSteps;

    @Before
    public void setUp() {
        login = "Igor_" + UUID.randomUUID();
        courierSteps = new CourierSteps();
    }

    @Test
    @DisplayName("Создание нового курьера")
    @Description("Проверяем, что курьера можно создать с валидными данными")
    public void createNewCourier() {

        CourierCreateRequest courierCreateRequest =
                new CourierCreateRequest(login, password, firstName);

        CourierLoginRequest courierLoginRequest =
                new CourierLoginRequest(login, password);

        try {
            courierSteps.courierCreate(courierCreateRequest)
                    .assertThat()
                    .statusCode(SC_CREATED)
                    .body("ok", equalTo(true));
        } finally {
            courierSteps.courierDeleteAfterLogin(courierLoginRequest);
        }
    }

    @Test
    @DisplayName("Создание двух одинаковых курьеров")
    @Description(
            "Попытка создать двух курьеров с одинаковым набором данных. " +
                    "Создание второго курьера должно провалиться"
    )
    public void createTwoIdenticalCouriers() {

        CourierCreateRequest courierCreateRequest =
                new CourierCreateRequest(login, password, firstName);

        CourierLoginRequest courierLoginRequest =
                new CourierLoginRequest(login, password);

        try {
            courierSteps.courierCreate(courierCreateRequest)
                    .assertThat()
                    .statusCode(SC_CREATED)
                    .body("ok", equalTo(true));

            courierSteps.courierCreate(courierCreateRequest)
                    .assertThat()
                    .statusCode(SC_CONFLICT)
                    .body(
                            "message",
                            equalTo("Этот логин уже используется. Попробуйте другой.")
                    );
        } finally {
            courierSteps.courierDeleteAfterLogin(courierLoginRequest);
        }
    }

    @Test
    @DisplayName("Создание курьера без логина")
    @Description(
            "Попытка создать курьера без передачи поля login. " +
                    "Создание курьера должно провалиться"
    )
    public void createCourierWithoutLogin() {

        CourierCreateRequest courierCreateRequest =
                new CourierCreateRequest(null, password, firstName);

        courierSteps.courierCreate(courierCreateRequest)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(
                        "message",
                        equalTo("Недостаточно данных для создания учетной записи")
                );
    }

    @Test
    @DisplayName("Создание курьера без пароля")
    @Description(
            "Попытка создать курьера без передачи поля password. " +
                    "Создание курьера должно провалиться"
    )
    public void createCourierWithoutPassword() {

        CourierCreateRequest courierCreateRequest =
                new CourierCreateRequest(login, null, firstName);

        courierSteps.courierCreate(courierCreateRequest)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body(
                        "message",
                        equalTo("Недостаточно данных для создания учетной записи")
                );
    }
}