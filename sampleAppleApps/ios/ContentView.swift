import SwiftUI
import sampleShared

struct ContentView: View {
	var body: some View {
		Text(GreetingKt.greeting)
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
