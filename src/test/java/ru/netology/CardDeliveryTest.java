package ru.netology;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {

    private Faker faker;

    @BeforeEach
    void setUp() {
        Configuration.browser = "chrome";
        Configuration.headless = false;
        Configuration.timeout = 15000;
        faker = new Faker(new Locale("ru"));
    }

    private String generateDate(int daysToAdd) {
        return LocalDate.now().plusDays(daysToAdd).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    private String generateName() {
        return faker.name().firstName() + " " + faker.name().lastName();
    }

    private String generatePhone() {
        return "+7" + faker.number().digits(10);
    }

    private void clearDateField() {
        $("[data-test-id='date'] input").doubleClick();
        $("[data-test-id='date'] input").sendKeys(Keys.BACK_SPACE);
    }

    @BeforeEach
    void openPage() {
        open("http://localhost:9999");
    }

    @Test
    void shouldSuccessfullySubmitForm() {
        String city = "Москва";
        String name = generateName();
        String phone = generatePhone();
        String date = generateDate(3);

        $("[data-test-id='city'] input").setValue(city);
        clearDateField();
        $("[data-test-id='date'] input").setValue(date);
        $("[data-test-id='name'] input").setValue(name);
        $("[data-test-id='phone'] input").setValue(phone);
        $("[data-test-id='agreement']").click();
        $(".button").click();

        $(".notification__content").shouldHave(Condition.text("Встреча успешно забронирована"));
    }

    @Test
    void shouldShowErrorForInvalidCity() {
        String city = "НесуществующийГород";
        String name = generateName();
        String phone = generatePhone();
        String date = generateDate(3);

        $("[data-test-id='city'] input").setValue(city);
        clearDateField();
        $("[data-test-id='date'] input").setValue(date);
        $("[data-test-id='name'] input").setValue(name);
        $("[data-test-id='phone'] input").setValue(phone);
        $("[data-test-id='agreement']").click();
        $(".button").click();

        $("[data-test-id='city'] .input__sub").shouldHave(Condition.text("Доставка в выбранный город недоступна"));
    }

    @Test
    void shouldShowErrorForPastDate() {
        String city = "Москва";
        String name = generateName();
        String phone = generatePhone();
        String date = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        $("[data-test-id='city'] input").setValue(city);
        clearDateField();
        $("[data-test-id='date'] input").setValue(date);
        $("[data-test-id='name'] input").setValue(name);
        $("[data-test-id='phone'] input").setValue(phone);
        $("[data-test-id='agreement']").click();
        $(".button").click();

        $("[data-test-id='date'] .input__sub").shouldHave(Condition.text("Заказ на выбранную дату невозможен"));
    }

    @Test
    void shouldShowErrorForNameWithNumbers() {
        String city = "Москва";
        String name = "Иван123 Петров";
        String phone = generatePhone();
        String date = generateDate(3);

        $("[data-test-id='city'] input").setValue(city);
        clearDateField();
        $("[data-test-id='date'] input").setValue(date);
        $("[data-test-id='name'] input").setValue(name);
        $("[data-test-id='phone'] input").setValue(phone);
        $("[data-test-id='agreement']").click();
        $(".button").click();

        $("[data-test-id='name'] .input__sub").shouldHave(Condition.text("Имя и Фамилия указаные неверно"));
    }

    @Test
    void shouldShowErrorForInvalidPhone() {
        String city = "Москва";
        String name = generateName();
        String phone = "1234567890";
        String date = generateDate(3);

        $("[data-test-id='city'] input").setValue(city);
        clearDateField();
        $("[data-test-id='date'] input").setValue(date);
        $("[data-test-id='name'] input").setValue(name);
        $("[data-test-id='phone'] input").setValue(phone);
        $("[data-test-id='agreement']").click();
        $(".button").click();

        $("[data-test-id='phone'] .input__sub").shouldHave(Condition.text("Телефон указан неверно"));
    }

    @Test
    void shouldShowErrorWhenAgreementNotChecked() {
        String city = "Москва";
        String name = generateName();
        String phone = generatePhone();
        String date = generateDate(3);

        $("[data-test-id='city'] input").setValue(city);
        clearDateField();
        $("[data-test-id='date'] input").setValue(date);
        $("[data-test-id='name'] input").setValue(name);
        $("[data-test-id='phone'] input").setValue(phone);
        $(".button").click();

        $("[data-test-id='agreement'].input_invalid").shouldBe(Condition.visible);
    }

    @Test
    void shouldSelectCityFromDropdown() {
        String city = "Казань";
        String name = generateName();
        String phone = generatePhone();
        String date = generateDate(7);

        $("[data-test-id='city'] input").setValue("Ка");
        $(".menu-item__control").shouldBe(Condition.visible);
        $$(".menu-item__control").findBy(Condition.text(city)).click();

        clearDateField();
        $("[data-test-id='date'] input").setValue(date);
        $("[data-test-id='name'] input").setValue(name);
        $("[data-test-id='phone'] input").setValue(phone);
        $("[data-test-id='agreement']").click();
        $(".button").click();

        $(".notification__content").shouldHave(Condition.text("Встреча успешно забронирована"));
    }
}