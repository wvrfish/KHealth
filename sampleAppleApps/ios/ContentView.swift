import SwiftUI
import sampleShared

struct ContentView: View {
	var body: some View {
        VStack(spacing: 24) {
            Button("Check All Permissions") {
                KHealthTestKt.checkAllPerms()
            }

            Button("Request All Permissions") {
                KHealthTestKt.requestAllPerms()
            }
        }
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
