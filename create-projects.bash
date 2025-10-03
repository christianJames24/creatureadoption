#!/usr/bin/env bash

spring init \
--boot-version=3.4.4 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=customers-service \
--package-name=com.creatureadoption.customers \
--groupId=com.creatureadoption.customer \
--dependencies=web,webflux,validation \
--version=1.0.0-SNAPSHOT \
customers-service

spring init \
--boot-version=3.4.4 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=creatures-service \
--package-name=com.creatureadoption.creatures \
--groupId=com.creatureadoption.creatures \
--dependencies=web,webflux,validation \
--version=1.0.0-SNAPSHOT \
creatures-service

spring init \
--boot-version=3.4.4 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=trainings-service \
--package-name=com.creatureadoption.trainings \
--groupId=com.creatureadoption.trainings \
--dependencies=web,webflux,validation \
--version=1.0.0-SNAPSHOT \
trainings-service

spring init \
--boot-version=3.4.4 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=adoptions-service \
--package-name=com.creatureadoption.adoptions \
--groupId=com.creatureadoption.adoptions \
--dependencies=web,webflux,validation \
--version=1.0.0-SNAPSHOT \
adoptions-service

spring init \
--boot-version=3.4.4 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=api-gateway \
--package-name=com.creatureadoption.apigateway \
--groupId=com.creatureadoption.apigateway \
--dependencies=web,webflux,validation,hateoas \
--version=1.0.0-SNAPSHOT \
api-gateway

