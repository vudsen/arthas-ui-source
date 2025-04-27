package io.github.vudsen.arthasui

import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.util.NlsContexts
import java.util.Stack
import java.util.concurrent.CancellationException

class TestProgressIndicator : ProgressIndicator {

    companion object {
        private enum class Status {
            CREATED,
            RUNNING,
            FINISHED,
            CANCELLED
        }
        private data class State(
            val text: String?,
            val text2: String?,
            val fraction: Double
        )
    }

    private var status = Status.CREATED

    private val stateStack = Stack<State>()

    private var currentText: String? = null

    private var currentText2: String? = null

    private var fraction = 0.0

    override fun start() {
        status = Status.RUNNING
    }

    override fun stop() {
        status = Status.FINISHED
    }

    override fun isRunning(): Boolean {
        return status == Status.RUNNING
    }

    override fun cancel() {
        status == Status.CANCELLED
    }

    override fun isCanceled(): Boolean {
        return status == Status.CANCELLED
    }

    override fun setText(text: @NlsContexts.ProgressText String?) {
        currentText = text
    }

    override fun getText(): @NlsContexts.ProgressText String? {
        return currentText
    }

    override fun setText2(text: @NlsContexts.ProgressDetails String?) {
        currentText2 = text
    }

    override fun getText2(): @NlsContexts.ProgressDetails String? {
        return currentText2
    }

    override fun getFraction(): Double {
        return fraction
    }

    override fun setFraction(fraction: Double) {
        this.fraction = fraction
    }

    override fun pushState() {
        stateStack.push(State(currentText, currentText2, fraction))
        currentText = null
        currentText2 = null
        fraction = 0.0
    }

    override fun popState() {
        val state = stateStack.pop()
        currentText = state.text
        currentText2 = state.text2
        fraction = state.fraction
    }

    override fun isModal(): Boolean {
        return false
    }

    override fun getModalityState(): ModalityState {
        return ModalityState.nonModal()
    }

    override fun setModalityProgress(modalityProgress: ProgressIndicator?) {

    }

    override fun isIndeterminate(): Boolean {
        return status == Status.FINISHED || status == Status.CANCELLED
    }

    override fun setIndeterminate(indeterminate: Boolean) {
        status = Status.FINISHED
    }

    override fun checkCanceled() {
        if (status == Status.CANCELLED) {
            throw CancellationException()
        }
    }

    override fun isPopupWasShown(): Boolean {
        return false
    }

    override fun isShowing(): Boolean {
        return false
    }
}