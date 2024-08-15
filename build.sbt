Global / onChangedBuildSource := ReloadOnSourceChanges

inThisBuild(Seq(
	organization	:= "de.djini",
	version			:= "0.2.0",

	scalaVersion	:= "3.3.1",
	scalacOptions	++= Seq(
		"-feature",
		"-deprecation",
		"-unchecked",
		"-source:future",
		"-Wunused:all",
		"-Xfatal-warnings",
		"-Ykind-projector:underscores",
	),

	versionScheme	:= Some("early-semver"),

	resolvers	++= Resolver sonatypeOssRepos "releases",

	wartremoverErrors	++= Seq(
		Wart.AsInstanceOf,
		Wart.IsInstanceOf,
		Wart.StringPlusAny,
		Wart.ToString,
		Wart.EitherProjectionPartial,
		Wart.OptionPartial,
		Wart.TryPartial,
		Wart.Enumeration,
		Wart.FinalCaseClass,
		Wart.JavaConversions,
		Wart.Option2Iterable,
		Wart.JavaSerializable,
		//Wart.Any,
		Wart.AnyVal,
		//Wart.Nothing,
		Wart.ArrayEquals,
		Wart.ImplicitParameter,
		Wart.ExplicitImplicitTypes,
		Wart.LeakingSealed,
		//Wart.DefaultArguments,
		//Wart.Overloading,
		//Wart.PublicInference,
		//Wart.TraversableOps,
		Wart.ListUnapply,
		Wart.ListAppend,
		Wart.GlobalExecutionContext,
		Wart.PlatformDefault,
	)
))

lazy val `sccontrol`	=
	project.in(file("."))
	.aggregate(
		`sccontrol-midi`,
		`sccontrol-osc`,
		`sccontrol-device`,
	)
	.settings(
		publishArtifact	:= false
	)

//------------------------------------------------------------------------------

lazy val `sccontrol-midi`	=
	project.in(file("modules/midi"))
	.enablePlugins()
	.settings(
		scalacOptions	++= Seq(),
		libraryDependencies	++= Seq(
			"de.djini"	%%	"scutil-jdk"	% "0.241.0"	% "compile",
			"io.monix"	%%	"minitest"		% "2.9.6"	% "test"
		),
		testFrameworks	+= new TestFramework("minitest.runner.Framework"),
	)
	.dependsOn()

lazy val `sccontrol-osc`	=
	project.in(file("modules/osc"))
	.enablePlugins()
	.settings(
		scalacOptions	++= Seq(),
		libraryDependencies	++= Seq(
			"de.djini"	%%	"scutil-jdk"	% "0.241.0"	% "compile",
			"de.djini"	%%	"scparse-ng"	% "0.250.0"	% "compile",
			"io.monix"	%%	"minitest"		% "2.9.6"	% "test"
		),
		testFrameworks	+= new TestFramework("minitest.runner.Framework"),
	)
	.dependsOn()

lazy val `sccontrol-device`	=
	project.in(file("modules/device"))
	.enablePlugins()
	.settings(
		scalacOptions	++= Seq(),
		libraryDependencies	++= Seq(
			"de.djini"	%%	"scutil-jdk"	% "0.241.0"	% "compile",
			"io.monix"	%%	"minitest"		% "2.9.6"				% "test"
		),
		testFrameworks	+= new TestFramework("minitest.runner.Framework"),
	)
	.dependsOn(
		`sccontrol-midi`
	)
