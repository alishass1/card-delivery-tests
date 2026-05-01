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
        Configuration.headless = Boolean.parseBoolean(System.getProperty("selenide.headless", "false"));
        faker = new Faker(new Locale("ru"));
    }

    private String generateDate(int daysToAdd) {
        return LocalDate.now()
                .plusDays(daysToAdd)
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    private void openPage() {
        open("http://localhost:9999");
    }

    private void fillForm(String city, String name, String phone, String date) {
        $("[data-test-id=city] input").setValue(city);
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(date);
        $("[data-test-id=name] input").setValue(name);
        $("[data-test-id=phone] input").setValue(phone);
        $("[data-test-id=agreement]").click();
    }

    private void submitForm() {
        $(".button").click();
    }

    private void checkSuccessNotification() {
        $(".notification__content")
                .shouldHave(Condition.text("Успешно!"), Condition.visible)
                .shouldBe(Condition.visible);
    }

    @Test
    void shouldSuccessfulFormSubmission() {
        String city = "Москва";
        String name = faker.name().fullName().replace("ё", "е");
        String phone = "+7" + faker.number().digits(10);
        String date = generateDate(3);

        openPage();
        fillForm(city, name, phone, date);
        submitForm();
        checkSuccessNotification();
    }

    @Test
    void shouldShowErrorForInvalidCity() {
        String city = "Санкт-Петербург";
        String name = faker.name().fullName().replace("ё", "е");
        String phone = "+7" + faker.number().digits(10);
        String date = generateDate(3);

        openPage();
        fillForm(city, name, phone, date);
        submitForm();

        $("[data-test-id=city].input_invalid .input__sub")
                .shouldHave(Condition.text("Доставка в выбранный город недоступна"));
    }

    @Test
    void shouldShowErrorForInvalidDateFormat() {
        String city = "Москва";
        String name = faker.name().fullName().replace("ё", "е");
        String phone = "+7" + faker.number().digits(10);
        String date = "01.01.2020";

        openPage();
        fillForm(city, name, phone, date);
        submitForm();

        $("[data-test-id=date] .input__sub")
                .shouldHave(Condition.text("Заказ на выбранную дату невозможен"));
    }

    @Test
    void shouldShowErrorForNameWithNumbers() {
        String city = "Москва";
        String name = "Иван123";
        String phone = "+7" + faker.number().digits(10);
        String date = generateDate(3);

        openPage();
        fillForm(city, name, phone, date);
        submitForm();

        $("[data-test-id=name].input_invalid .input__sub")
                .shouldHave(Condition.text("Имя и Фамилия указаные неверно"));
    }

    @Test
    void shouldShowErrorForInvalidPhone() {
        String city = "Москва";
        String name = faker.name().fullName().replace("ё", "е");
        String phone = "1234567890";
        String date = generateDate(3);

        openPage();
        fillForm(city, name, phone, date);
        submitForm();

        $("[data-test-id=phone].input_invalid .input__sub")
                .shouldHave(Condition.text("Мобильный телефон указан неверно"));
    }
}