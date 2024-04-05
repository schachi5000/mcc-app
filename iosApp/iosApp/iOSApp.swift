import SwiftUI
import shared

@main
struct iOSApp: App {
	var body: some Scene {
		WindowGroup {
            ContentView().onOpenURL(perform: { url in
                TestClass.shared.test(text: url.absoluteString)
            })
        }
	}
}
