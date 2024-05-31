import java.text.SimpleDateFormat
import java.util.*

plugins {
	id("eclipse")
	id("idea")
	id("maven-publish")
	id("net.neoforged.gradle").version("[6.0.18,6.2)")
	id("org.parchmentmc.librarian.forgegradle").version("1.+")
	id("org.spongepowered.mixin")
}

val minecraftVersion: String by extra
val minecraftVersionRange: String by extra
val loaderVersionRange: String by extra
val neoforgeVersionRange: String by extra
val modVersion: String by extra
val modGroupId: String by extra
val modId: String by extra
val modAuthors: String by extra
val modDescription: String by extra
val modLicense: String by extra
val modName: String by extra
val parchmentChannel: String by extra
val parchmentVersion: String by extra
val neoforgeVersion: String by extra
val jeiVersion: String by extra
val mixinVersion: String by extra
val modJavaVersion: String by extra
val registrateVersion: String by extra
val terraBlenderVersion: String by extra
val cyanideVersion: String by extra
val arsNouveauVersion: String by extra
val curiosVersion: String by extra

version = "$minecraftVersion-$modVersion"
if (System.getenv("BUILD_NUMBER") != null) {
	version = "$minecraftVersion-$modVersion.${System.getenv("BUILD_NUMBER")}"
}
group = modGroupId

val baseArchivesName = modId
base {
	archivesName.set(baseArchivesName)
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(modJavaVersion))
	}
	withSourcesJar()
}

jarJar.enable()

mixin {
	add(sourceSets.main.get(), "hexerei.refmap.json")
}

minecraft {
	mappings(parchmentChannel, parchmentVersion)

	copyIdeResources.set(true)

	accessTransformer("src/main/resources/META-INF/accesstransformer.cfg")

	// Default run configurations.
	// These can be tweaked, removed, or duplicated as needed.
	runs {
		// applies to all the run configs below
		configureEach {
			workingDirectory(project.file("run"))

			property("forge.logging.markers", "REGISTRIES")
			property("forge.logging.console.level", "debug")
			arg("-mixin.config=$modId.mixins.json")

			mods {
				create(modId) {
					source(sourceSets.main.get())
				}
			}
		}

		create("client") {
			// Comma-separated list of namespaces to load gametests from. Empty = all namespaces.
			property("forge.enabledGameTestNamespaces", modId)
		}

		create("server") {
			property("forge.enabledGameTestNamespaces", modId)
			args("--nogui")
		}

		create("data") {
			// example of overriding the workingDirectory set in configureEach above
			workingDirectory(project.file("run-data"))

			// Specify the modid for data generation, where to output the resulting resource, and where to look for existing resources.
			args(
				"--mod",
				modId,
				"--all",
				"--output",
				file("src/generated/resources/"),
				"--existing",
				file("src/main/resources/")
			)
		}
	}
}

sourceSets {
	main {
		resources.srcDirs("src/generated/resources")
	}
}

repositories {
	mavenCentral()
	maven {
		name = "Curios maven"
		url = uri("https://maven.theillusivec4.top/")
	}
	maven {
		name = "JEI maven"
		url = uri("https://dvs1.progwml6.com/files/maven")
	}
	maven {
		name = "tterrag maven"
		url = uri("https://maven.tterrag.com/")
	}
	maven {
		name = "BlameJared maven"
		url = uri("https://maven.blamejared.com/")
	}
	maven {
		name = "Curse Maven"
		url = uri("https://cursemaven.com")
		content {
			includeGroup("curse.maven")
		}
	}
	maven {
		name = "Thermal Maven"
		url = uri("https://maven.covers1624.net/")
	}
	maven {
		name = "Kotlin for Forge"
		setUrl("https://thedarkcolour.github.io/KotlinForForge/")
	}
	maven {
		name = "Gecko Lib"
		setUrl("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
	}
}

dependencies {
	minecraft("net.neoforged:forge:$minecraftVersion-$neoforgeVersion")

	if (System.getProperty("idea.sync.active") != "true") {
		annotationProcessor("org.spongepowered:mixin:$mixinVersion:processor")
	}

	// JEI Dependency
	compileOnly(fg.deobf("mezz.jei:jei-${minecraftVersion}-common-api:${jeiVersion}"))
	compileOnly(fg.deobf("mezz.jei:jei-${minecraftVersion}-forge-api:${jeiVersion}"))
	runtimeOnly(fg.deobf("mezz.jei:jei-${minecraftVersion}-forge:${jeiVersion}"))


	jarJar(group = "com.tterrag.registrate", name = "Registrate", version = "[$registrateVersion,)") {
		jarJar.pin(this, registrateVersion)
	}
//
	implementation(fg.deobf("com.tterrag.registrate:Registrate:${registrateVersion}"))

	implementation (fg.deobf("curse.maven:terrablender-563928:$terraBlenderVersion")) // Terra Blender
	implementation(fg.deobf("curse.maven:cyanide-541676:$cyanideVersion")) // Cyanide
//	implementation(fg.deobf("curse.maven:spit-it-out-857141:4888754")) // Spit It Out

	/*implementation*/ compileOnly(fg.deobf("com.hollingsworth.ars_nouveau:ars_nouveau-${minecraftVersion}:${arsNouveauVersion}"))
	compileOnly(fg.deobf("top.theillusivec4.curios:curios-forge:${curiosVersion}:api"))
	runtimeOnly(fg.deobf("top.theillusivec4.curios:curios-forge:${curiosVersion}"))
}


tasks.withType<ProcessResources> {
	inputs.property("version", version)

	filesMatching(listOf("META-INF/mods.toml", "pack.mcmeta")) {
		expand(
			mapOf(
				"forgeVersionRange" to neoforgeVersionRange,
				"loaderVersionRange" to loaderVersionRange,
				"minecraftVersion" to minecraftVersion,
				"minecraftVersionRange" to minecraftVersionRange,
				"modAuthors" to modAuthors,
				"modDescription" to modDescription,
				"modId" to modId,
				"modJavaVersion" to modJavaVersion,
				"modName" to modName,
				"modVersion" to version,
				"modLicense" to modLicense,
			)
		)
	}
}

tasks.withType<Jar> {
	val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())
	manifest {
		attributes(
			mapOf(
				"Specification-Title" to modName,
				"Specification-Vendor" to modAuthors,
				"Specification-Version" to '1',
				"Implementation-Title" to modName,
				"Implementation-Version" to version,
				"Implementation-Vendor" to modAuthors,
				"Implementation-Timestamp" to now,
			)
		)
	}
	finalizedBy("reobfJar")
}

publishing {
	publications {
		register<MavenPublication>("mavenJava") {
			artifactId = baseArchivesName
			artifact(tasks.jar.get())
		}
	}
	repositories {
		maven {
			url = uri("file://${System.getenv("local_maven")}")
		}
	}
}

idea {
	module {
		for (fileName in listOf("run", "out", "logs")) {
			excludeDirs.add(file(fileName))
		}
	}
}

tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
}

tasks.register("jarJarRelease") {
	doLast {
		tasks.jarJar {
			(this as Jar).archiveClassifier.set("")
		}
	}
	finalizedBy(tasks.getByName("jarJar"))
}
tasks.jarJar.get().finalizedBy("reobfJarJar")