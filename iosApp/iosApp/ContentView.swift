import UIKit
import SwiftUI
import shared
import SafariServices


struct ContentView: View {
    @State private var showingSheet = false
    
    var body: some View {
        var loginBridge : LoginBridge! = nil
       
        ComposeView(onOpenLoginPage: { it in
            loginBridge = it
            showingSheet.toggle()
        })
                .onOpenURL(perform: { url in
                    if(url.absoluteString.contains("mccapp://callback")){
                        showingSheet = false
                        loginBridge.onLoginSuccessful(callbackUrl: url.absoluteString)
                    }
                })
                .edgesIgnoringSafeArea(.all)
                .ignoresSafeArea(.all, edges: .bottom)
                .sheet(isPresented: $showingSheet) {
                    SafariWebView(url: URL(string: loginBridge.url)!).ignoresSafeArea()
                }
    }
}

struct ComposeView: UIViewControllerRepresentable {
    var onOpenLoginPage = {  (loginBridge: LoginBridge) in}
    
    func makeUIViewController(context: Context) -> UIViewController {
        Main_iosKt.MainViewController(onLogInClicked: { (loginBridge: LoginBridge) in
            onOpenLoginPage(loginBridge)
        })
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}


struct SafariWebView: UIViewControllerRepresentable {
    let url: URL
    
    func makeUIViewController(context: Context) -> SFSafariViewController {
        return SFSafariViewController(url: url)
    }
    
    func updateUIViewController(_ uiViewController: SFSafariViewController, context: Context) {}
}
