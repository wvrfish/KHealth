import SwiftUI
import sampleShared

struct ContentView: View {
	var body: some View {
        VStack(spacing: 24) {
            Button("Check All Permissions") {
                KHealthSampleKt.checkAllPerms()
            }

            Button("Request All Permissions") {
                KHealthSampleKt.requestAllPerms()
            }
        }
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
