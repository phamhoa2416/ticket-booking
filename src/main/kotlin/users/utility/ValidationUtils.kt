package users.utility

import events.models.types.EventCategory
import java.time.LocalDate
import users.exceptions.ValidationException
import users.models.dto.PaymentMethodDTO
import java.math.BigDecimal
import java.util.regex.Pattern

object ValidationUtils {
    private const val MIN_PASSWORD_LENGTH = 8
    private const val MAX_PASSWORD_LENGTH = 100
    private const val MIN_USERNAME_LENGTH = 3
    private const val MAX_USERNAME_LENGTH = 50

    private val PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")
    private val USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{3,50}$")
    private val PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{10,15}\$")
    private val EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$")
    private val ORGANIZATION_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s-]{3,100}$")
    private val TAX_ID_PATTERN = Pattern.compile("^[A-Z0-9]{10,20}$")

    fun validateEmail(email: String) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw ValidationException("email", "Invalid email format")
        }
    }

    fun validatePassword(password: String) {
        if (password.length < MIN_PASSWORD_LENGTH || password.length > MAX_PASSWORD_LENGTH) {
            throw ValidationException(
                "password",
                "Password must be between $MIN_PASSWORD_LENGTH and $MAX_PASSWORD_LENGTH characters"
            )
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw ValidationException(
                "password",
                "Password must contain at least one digit, one lowercase letter, " +
                        "one uppercase letter, and one special character"
            )
        }
    }

    fun validatePhoneNumber(phoneNumber: String) {
        if (!PHONE_PATTERN.matcher(phoneNumber).matches()) {
            throw ValidationException("phoneNumber", "Invalid phone number format")
        }
    }

    fun validateUsername(username: String) {
        if (username.length < MIN_USERNAME_LENGTH || username.length > MAX_USERNAME_LENGTH) {
            throw ValidationException(
                "username",
                "Username must be between $MIN_USERNAME_LENGTH and $MAX_USERNAME_LENGTH characters"
            )
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw ValidationException(
                "username",
                "Username can only contain letters, numbers, underscores, and hyphens"
            )
        }
    }

    fun validateDateOfBirth(dateOfBirth: LocalDate?) {
        dateOfBirth?.let {
            val today = LocalDate.now()
            val minAge = 13
            val maxAge = 120

            if (it.isAfter(today)) {
                throw ValidationException("dateOfBirth", "Date of birth cannot be in the future")
            }

            val age = today.year - it.year - (if (today.dayOfYear < it.dayOfYear) 1 else 0)
            if (age < minAge || age > maxAge) {
                throw ValidationException("dateOfBirth", "Age must be between $minAge and $maxAge years")
            }
        }
    }

    fun validateOrganizationName(organizationName: String) {
        if (!ORGANIZATION_NAME_PATTERN.matcher(organizationName).matches()) {
            throw ValidationException(
                "organizationName",
                "Organization name must be between 3 and 100 characters " +
                        "and can only contain letters, numbers, spaces, and hyphens"
            )
        }
    }

    fun validateTaxId(taxId: String?) {
        taxId?.let {
            if (!TAX_ID_PATTERN.matcher(it).matches()) {
                throw ValidationException(
                    "taxId",
                    "Tax ID must be between 10 and 20 characters and can only contain uppercase letters and numbers"
                )
            }
        }
    }

    fun validatePaymentMethod(paymentMethod: PaymentMethodDTO) {
        when (paymentMethod) {
            is PaymentMethodDTO.CreditCardDTO -> {
                if (paymentMethod.last4.length != 4) {
                    throw ValidationException("last4", "Last 4 digits must be exactly 4 characters")
                }
                if (paymentMethod.brand.isBlank()) {
                    throw ValidationException("brand", "Brand cannot be empty")
                }
                if (paymentMethod.expiration.isBlank()) {
                    throw ValidationException("expiration", "Expiration date cannot be empty")
                }
            }
            is PaymentMethodDTO.PayPalDTO -> {
                if (!EMAIL_PATTERN.matcher(paymentMethod.email).matches()) {
                    throw ValidationException("email", "Invalid email format")
                }
            }
        }
    }

    fun validateLoyaltyPoints(loyaltyPoints: Int?) {
        loyaltyPoints?.let {
            if (it < 0) {
                throw ValidationException("loyaltyPoints", "Loyalty points cannot be negative")
            }
        }
    }

    fun validateTotalSpending(amount: BigDecimal) {
        if (amount < BigDecimal.ZERO) {
            throw ValidationException("totalSpending", "Total spending cannot be negative")
        }
    }

    fun validateCategory(category: EventCategory) {
        if (category == EventCategory.UNKNOWN) {
            throw ValidationException("category", "Invalid event category")
        }
    }

    fun validateRating(rating: BigDecimal) {
        if (rating < BigDecimal.ZERO || rating > BigDecimal(5)) {
            throw ValidationException("rating", "Rating must be between 0 and 5")
        }
    }
}