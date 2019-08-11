package at.htl_leonding.roarfit.services

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import at.htl_leonding.roarfit.activities.AuthActivity
import at.htl_leonding.roarfit.data.LoginRequest
import at.htl_leonding.roarfit.network.WebService
import at.htl_leonding.roarfit.network.WebServiceFactory
import at.htl_leonding.roarfit.utils.Constants

class Authenticator(val context: Context) : AbstractAccountAuthenticator(context) {

    override fun addAccount(
        response: AccountAuthenticatorResponse?,
        accountType: String?,
        authTokenType: String?,
        requiredFeatures: Array<out String>?,
        options: Bundle?
    ): Bundle {
        val intent = Intent(context, AuthActivity::class.java)
        intent.putExtra(Constants.ACCOUNT_TYPE, accountType)
        intent.putExtra("full_access", authTokenType)
        intent.putExtra("is_adding_new_account", true)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)

        val bundle = Bundle()
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)
        return bundle
    }

    override fun getAuthToken(
        response: AccountAuthenticatorResponse?,
        account: Account,
        authTokenType: String?,
        options: Bundle?
    ): Bundle {
        val am = AccountManager.get(context)
        var authToken = am.peekAuthToken(account, authTokenType)

        if (TextUtils.isEmpty(authToken)) {
            val webService: WebService = WebServiceFactory.create()
            val loginRes = webService.getToken(LoginRequest(account.name, am.getPassword(account)))
            authToken = loginRes.token
        }

        if (!TextUtils.isEmpty(authToken)) {
            val result = Bundle()
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken)
            return result
        }

        val intent = Intent(context, AuthActivity::class.java)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
        intent.putExtra(Constants.ACCOUNT_TYPE, account.type)
        intent.putExtra("full_access", authTokenType)

        val retBundle = Bundle()
        retBundle.putParcelable(AccountManager.KEY_INTENT, intent)
        return retBundle
    }

    override fun getAuthTokenLabel(authTokenType: String?): String {
        throw UnsupportedOperationException()
    }

    override fun confirmCredentials(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        options: Bundle?
    ): Bundle {
        throw UnsupportedOperationException()
    }

    override fun updateCredentials(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        authTokenType: String?,
        options: Bundle?
    ): Bundle {
        throw UnsupportedOperationException()
    }

    override fun hasFeatures(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        features: Array<out String>?
    ): Bundle {
        throw UnsupportedOperationException()
    }

    override fun editProperties(response: AccountAuthenticatorResponse?, accountType: String?): Bundle {
        throw UnsupportedOperationException()
    }

}
