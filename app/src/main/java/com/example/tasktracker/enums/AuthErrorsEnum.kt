package com.example.tasktracker.enums

import android.util.Log
import com.google.firebase.auth.FirebaseAuthException

enum class AuthErrorsEnum(val value: String) {
    // Ошибки Firebase
    ERROR_INVALID_CREDENTIAL("Указанные данные неверны или срок их действия истек"),
    ERROR_INVALID_EMAIL("Некорректный адрес электронной почты!"),
    ERROR_WRONG_PASSWORD("Неверный пароль или пароль не задан!"),
    ERROR_USER_MISMATCH(
        "Предоставленные учетные данные не соответствуют ранее зарегистрированному пользователю"
    ),
    ERROR_REQUIRES_RECENT_LOGIN(
        "Ошибка авторизации. Войдите в систему еще раз, прежде чем повторить этот запрос"
    ),
    ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL(
        "Учетная запись с таким же адресом электронной почты уже существует, но другими учетными данными для входа"
    ),
    ERROR_EMAIL_ALREADY_IN_USE("Адрес электронной почты уже используется!"),
    ERROR_CREDENTIAL_ALREADY_IN_USE(
        "Эти учетные данные уже связаны с другой учетной записью пользователя!"
    ),
    ERROR_USER_DISABLED("Учетная запись пользователя была отключена администратором!"),
    ERROR_USER_TOKEN_EXPIRED(
        "Срок действия учетных данных пользователя истек! Выполните вход повторно"
    ),
    ERROR_USER_NOT_FOUND(
        "Нет записи пользователя, соответствующей этому идентификатору. Возможно, пользователь был удален"
    ),
    ERROR_INVALID_USER_TOKEN(
        "Учетные данные пользователя больше недействительны! Выполните вход повторно"
    ),
    ERROR_OPERATION_NOT_ALLOWED("Эта операция запрещена! Обратитесь к администратору!"),
    ERROR_WEAK_PASSWORD(
        "Указанный пароль слишком слабый, пожалуйста, выберите более надежный пароль."
    ),
    ERROR_EXPIRED_ACTION_CODE("Срок действия кода истек!"),
    ERROR_INVALID_ACTION_CODE(
        "Код недопустим. Это может произойти, если код неправильно сформирован, срок действия истек или уже использовался"
    ),
    ERROR_INVALID_MESSAGE_PAYLOAD(
        "Шаблон электронной почты, соответствующий этому действию, содержит недопустимые символы в своем сообщении. Обратитесь к администратору"
    ),
    ERROR_INVALID_RECIPIENT_EMAIL(
        "Электронное письмо не удалось отправить, поскольку указанный адрес электронной почты получателя неверен."
    ),
    ERROR_INVALID_SENDER(
        "Шаблон электронной почты, соответствующий этому действию, содержит недопустимый адрес электронной почты или имя отправителя. Обратитесь к администратору"
    ),
    ERROR_MISSING_EMAIL("Необходимо указать адрес электронной почты!"),
    ERROR_MISSING_PASSWORD("Необходимо указать пароль!"),
    ERROR_MISSING_PHONE_NUMBER("Укажите телефон, чтобы отправить проверочный код"),
    ERROR_INVALID_PHONE_NUMBER("Указанный формат телефонного номера неверен!"),
    ERROR_MISSING_VERIFICATION_CODE(
        "Учетные данные для авторизации телефона были созданы с помощью пустого sms-кода подтверждения"
    ),
    ERROR_INVALID_VERIFICATION_CODE(
        "SMS-код подтверждения, использованный для создания учетных данных для авторизации на телефоне, недействителен. Пожалуйста, повторно отправьте sms-сообщение с кодом подтверждения"
    ),
    ERROR_MISSING_VERIFICATION_ID(
        "Учетные данные для авторизации телефона были созданы с пустым идентификатором подтверждения"
    ),
    ERROR_INVALID_VERIFICATION_ID("Идентификатор подтверждения авторизации недействителен"),
    ERROR_RETRY_PHONE_AUTH("Произошла ошибка аутентификации"),
    ERROR_SESSION_EXPIRED(
        "Срок действия sms-кода истек. Пожалуйста, отправьте повторно проверочный код"
    ),
    ERROR_QUOTA_EXCEEDED("SMS квота была превышена"),
    ERROR_API_NOT_AVAILABLE(
        "Запрос не может быть выполнен из-за отсутствия на устройстве сервисов Google Play"
    ),
    ERROR_WEB_CONTEXT_CANCELED("Операция была отменена пользователем"),
    ERROR_TOO_MANY_REQUESTS("Слишком часто, попробуйте позднее"),
    ERROR_UNKNOWN("Произошла неизвестная ошибка"),
    ERROR_DEFAULT("Ошибка выполнения запроса");

    companion object {
        fun getErrorMessage(error: FirebaseAuthException): String {
            return try {
                valueOf(error.errorCode).value
            } catch (e: IllegalArgumentException) {
                Log.e("getErrorMessage", "Unknown Error")
                ERROR_UNKNOWN.value
            }
        }
    }
}
