package com.zone.messanger.text.PANDA_ads

object AdCycleController {

    private var currentStep = AdCycleStep.INTERSTITIAL

    fun nextStep() {
        currentStep = when (currentStep) {
            AdCycleStep.INTERSTITIAL -> AdCycleStep.APP_OPEN
            AdCycleStep.APP_OPEN -> AdCycleStep.FULLSCREEN_NATIVE
            AdCycleStep.FULLSCREEN_NATIVE -> AdCycleStep.INTERSTITIAL
        }
    }

    fun getStep(): AdCycleStep = currentStep

    fun reset() {
        currentStep = AdCycleStep.INTERSTITIAL
    }
}