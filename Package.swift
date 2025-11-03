// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "RoutalHereNavigation",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "RoutalHereNavigation",
            targets: ["HereNavigationPluginPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "7.0.0")
    ],
    targets: [
        .target(
            name: "HereNavigationPluginPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/HereNavigationPluginPlugin"),
        .testTarget(
            name: "HereNavigationPluginPluginTests",
            dependencies: ["HereNavigationPluginPlugin"],
            path: "ios/Tests/HereNavigationPluginPluginTests")
    ]
)