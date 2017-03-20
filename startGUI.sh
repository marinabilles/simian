#!/usr/bin/env bash
mvn compile
mvn exec:java -Dexec.mainClass="de.crispda.sola.multitester.runner.ExperimentRunner"
