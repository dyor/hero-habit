import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController
import com.dyor.habithero.root.App
import com.dyor.habithero.util.LocalNativeViewFactory
import com.dyor.habithero.util.NativeViewFactory
import com.dyor.habithero.util.SwiftLibDependencyFactory
import com.dyor.habithero.util.swiftLibDependenciesModule
import org.koin.core.KoinApplication
import platform.UIKit.UIViewController

fun MainViewController(nativeViewFactory: NativeViewFactory): UIViewController = ComposeUIViewController {
    CompositionLocalProvider(LocalNativeViewFactory provides nativeViewFactory) {
        App()
    }
}

// This is called on application started on Swift side
fun KoinApplication.provideSwiftLibDependencyFactory(factory: SwiftLibDependencyFactory) = run { modules(swiftLibDependenciesModule(factory)) }
