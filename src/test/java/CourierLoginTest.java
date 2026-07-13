import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.example.pojo.CourierCreateRequest;
import org.example.pojo.CourierLoginRequest;
import org.example.steps.CourierSteps;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;

public class CourierLoginTest {

    private CourierSteps courierSteps;
    private CourierLoginRequest validCourierLoginRequest;

    private String login;
    private final String password = "qwerty123";

    @Before
    public void setUp() {
        courierSteps = new CourierSteps();
        login = "Igor_" + UUID.randomUUID();

        CourierCreateRequest courierCreateRequest =
                new CourierCreateRequest(login, password, "Игорь");

        validCourierLoginRequest =
                new CourierLoginRequest(login, password);

        courierSteps.courierCreate(courierCreateRequest)
                .log().all()
                .assertThat()
                .statusCode(201);
    }

    @After
    public void tearDown() {
        if (courierSteps != null && validCourierLoginRequest != null) {
            courierSteps.courierDeleteAfterLogin(validCourierLoginRequest);
        }
    }

    @Test
    @DisplayName("Авторизация курьера")
    @Description("Проверка, что курьер может авторизоваться с валидными данными")
    public void loginCourier() {
        courierSteps.courierLogin(validCourierLoginRequest)
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("id", instanceOf(Integer.class));
    }

    @Test
    @DisplayName("Авторизация курьера без логина")
    @Description("Проверка, что курьер не может авторизоваться без поля login")
    public void loginCourierWithoutLogin() {
        CourierLoginRequest requestWithoutLogin =
                new CourierLoginRequest(null, password);

        courierSteps.courierLogin(requestWithoutLogin)
                .log().all()
                .assertThat()
                .statusCode(400)
                .body(
                        "message",
                        equalTo("Недостаточно данных для входа")
                );
    }

    @Test
    @DisplayName("Авторизация курьера без пароля")
    @Description("Проверка, что курьер не может авторизоваться без поля password")
    public void loginCourierWithoutPassword() {
        CourierLoginRequest requestWithoutPassword =
                new CourierLoginRequest(login, null);

        courierSteps.courierLogin(requestWithoutPassword)
                .log().all()
                .assertThat()
                .statusCode(400)
                .body(
                        "message",
                        equalTo("Недостаточно данных для входа")
                );
    }

    @Test
    @DisplayName("Авторизация курьера с неверным логином")
    @Description("Проверка авторизации с несуществующим логином")
    public void loginCourierWithWrongLogin() {
        CourierLoginRequest requestWithWrongLogin =
                new CourierLoginRequest(login + "_wrong", password);

        courierSteps.courierLogin(requestWithWrongLogin)
                .log().all()
                .assertThat()
                .statusCode(404)
                .body(
                        "message",
                        equalTo("Учетная запись не найдена")
                );
    }

    @Test
    @DisplayName("Авторизация курьера с неверным паролем")
    @Description("Проверка авторизации с неверным паролем")
    public void loginCourierWithWrongPassword() {
        CourierLoginRequest requestWithWrongPassword =
                new CourierLoginRequest(login, password + "_wrong");

        courierSteps.courierLogin(requestWithWrongPassword)
                .log().all()
                .assertThat()
                .statusCode(404)
                .body(
                        "message",
                        equalTo("Учетная запись не найдена")
                );
    }
}