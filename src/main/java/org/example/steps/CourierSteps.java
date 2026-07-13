package org.example.steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.example.constants.ApiEndpoint;
import org.example.pojo.CourierCreateRequest;
import org.example.pojo.CourierLoginRequest;
import org.example.pojo.CourierLoginResponse;

import static io.restassured.RestAssured.given;
import static org.example.constants.ApiEndpoint.COURIER_DELETE;
import static org.example.constants.ApiEndpoint.COURIER_POST_CREATE;
import static org.example.constants.ApiEndpoint.COURIER_POST_LOGIN;

public class CourierSteps {

    public static RequestSpecification requestSpecification() {
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .baseUri(ApiEndpoint.BASE_URL);
    }

    @Step("Создание нового курьера")
    public ValidatableResponse courierCreate(
            CourierCreateRequest courierCreateRequest
    ) {
        return requestSpecification()
                .body(courierCreateRequest)
                .post(COURIER_POST_CREATE)
                .then()
                .log().all();
    }

    @Step("Логин курьера")
    public ValidatableResponse courierLogin(
            CourierLoginRequest courierLoginRequest
    ) {
        return requestSpecification()
                .body(courierLoginRequest)
                .post(COURIER_POST_LOGIN)
                .then()
                .log().all();
    }

    @Step("Удаление курьера с ID: {courierId}")
    public void courierDelete(int courierId) {
        requestSpecification()
                .delete(COURIER_DELETE + courierId)
                .then()
                .log().all();
    }

    @Step("Логин курьера, получение ID и удаление курьера")
    public void courierDeleteAfterLogin(
            CourierLoginRequest courierLoginRequest
    ) {
        Response response = courierLogin(courierLoginRequest)
                .extract()
                .response();

        CourierLoginResponse courierLoginResponse =
                response.as(CourierLoginResponse.class);

        int courierId = courierLoginResponse.getId();

        courierDelete(courierId);
    }
}