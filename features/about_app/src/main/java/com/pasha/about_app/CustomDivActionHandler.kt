package com.pasha.about_app


import com.google.android.material.snackbar.Snackbar
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.DivViewFacade
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction

class CustomDivActionHandler(
    private val navigationBackAction: () -> Unit
): DivActionHandler() {
    override fun handleAction(
        action: DivAction,
        view: DivViewFacade,
        resolver: ExpressionResolver
    ): Boolean {
        if (super.handleAction(action, view, resolver)) {
            return true
        }

        val uri = action.url?.evaluate(view.expressionResolver) ?: return false

        if (uri.authority == "navigate-back" && uri.scheme == "navigation") {
            navigationBackAction.invoke()

            return true
        }

        if (uri.authority != "send-to-telegram" || uri.scheme != "notification") return false
        val text = uri.getQueryParameter("text") ?: return false

        Snackbar.make(view.view, text, Snackbar.LENGTH_LONG).also {
            it.setTextMaxLines(5)
        }.show()
        return true
    }
}