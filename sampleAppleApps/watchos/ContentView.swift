//
//  ContentView.swift
//  sampleWatchOSApp Watch App
//
//  Created by ss on 07/11/24.
//  Copyright © 2024 orgName. All rights reserved.
//

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

#Preview {
    ContentView()
}
