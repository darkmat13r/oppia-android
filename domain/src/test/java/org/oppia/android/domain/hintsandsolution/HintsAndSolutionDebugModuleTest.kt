package org.oppia.android.domain.hintsandsolution

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.oppia.android.domain.hintsandsolution.HintHandlerDebugImpl.FactoryDebugImpl
import org.oppia.android.testing.TestLogReportingModule
import org.oppia.android.testing.robolectric.RobolectricModule
import org.oppia.android.testing.threading.TestDispatcherModule
import org.oppia.android.util.data.DataProvidersInjector
import org.oppia.android.util.data.DataProvidersInjectorProvider
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import javax.inject.Inject
import javax.inject.Singleton

/** Tests for [HintsAndSolutionDebugModule]. */
@Suppress("FunctionName")
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(application = HintsAndSolutionDebugModuleTest.TestApplication::class)
class HintsAndSolutionDebugModuleTest {
  @Inject
  lateinit var hintHandlerFactory: HintHandler.Factory

  @Before
  fun setUp() {
    setUpTestApplicationComponent()
  }

  @Test
  fun testHintHandlerFactoryInjection_providesFactoryDebugImpl() {
    assertThat(hintHandlerFactory).isInstanceOf(FactoryDebugImpl::class.java)
  }

  private fun setUpTestApplicationComponent() {
    ApplicationProvider.getApplicationContext<TestApplication>().inject(this)
  }

  // TODO(#89): Move this to a common test application component.
  @Module
  interface TestModule {
    @Binds
    fun provideContext(application: Application): Context
  }

  @Singleton
  @Component(
    modules = [
      TestModule::class, HintsAndSolutionDebugModule::class, HintsAndSolutionConfigModule::class,
      TestLogReportingModule::class, TestDispatcherModule::class, RobolectricModule::class
    ]
  )
  interface TestApplicationComponent : DataProvidersInjector {
    @Component.Builder
    interface Builder {
      @BindsInstance
      fun setApplication(application: Application): Builder

      fun build(): TestApplicationComponent
    }

    fun inject(hintsAndSolutionDebugModuleTest: HintsAndSolutionDebugModuleTest)
  }

  class TestApplication : Application(), DataProvidersInjectorProvider {
    private val component: TestApplicationComponent by lazy {
      DaggerHintsAndSolutionDebugModuleTest_TestApplicationComponent.builder()
        .setApplication(this)
        .build()
    }

    fun inject(hintsAndSolutionDebugModuleTest: HintsAndSolutionDebugModuleTest) {
      component.inject(hintsAndSolutionDebugModuleTest)
    }

    override fun getDataProvidersInjector(): DataProvidersInjector = component
  }
}
