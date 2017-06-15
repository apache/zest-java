/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package org.apache.polygene.test.entity.model;

import org.apache.polygene.api.association.NamedAssociation;
import org.apache.polygene.api.common.Visibility;
import org.apache.polygene.api.constraint.ConstraintViolationException;
import org.apache.polygene.api.identity.Identity;
import org.apache.polygene.api.injection.scope.Service;
import org.apache.polygene.api.injection.scope.Structure;
import org.apache.polygene.api.object.ObjectFactory;
import org.apache.polygene.api.structure.Application;
import org.apache.polygene.api.structure.ApplicationDescriptor;
import org.apache.polygene.api.structure.Module;
import org.apache.polygene.api.unitofwork.NoSuchEntityException;
import org.apache.polygene.api.unitofwork.UnitOfWork;
import org.apache.polygene.api.unitofwork.UnitOfWorkFactory;
import org.apache.polygene.api.usecase.UsecaseBuilder;
import org.apache.polygene.bootstrap.ApplicationAssembly;
import org.apache.polygene.bootstrap.AssemblyException;
import org.apache.polygene.bootstrap.LayerAssembly;
import org.apache.polygene.bootstrap.ModuleAssembly;
import org.apache.polygene.entitystore.memory.MemoryEntityStoreService;
import org.apache.polygene.spi.serialization.JsonSerialization;
import org.apache.polygene.test.AbstractPolygeneBaseTest;
import org.apache.polygene.test.entity.model.legal.LegalService;
import org.apache.polygene.test.entity.model.legal.Will;
import org.apache.polygene.test.entity.model.legal.WillAmount;
import org.apache.polygene.test.entity.model.legal.WillItem;
import org.apache.polygene.test.entity.model.legal.WillPercentage;
import org.apache.polygene.test.entity.model.people.Address;
import org.apache.polygene.test.entity.model.people.City;
import org.apache.polygene.test.entity.model.people.Country;
import org.apache.polygene.test.entity.model.people.PeopleRepository;
import org.apache.polygene.test.entity.model.people.Person;
import org.apache.polygene.test.entity.model.people.PhoneNumber;
import org.apache.polygene.test.entity.model.people.Rent;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public abstract class EntityStoreTestSuite extends AbstractPolygeneBaseTest
{
    private static final String FRIEND = "Friend";
    private static final String COLLEAGUE = "Colleague";

    protected ModuleAssembly configModule;

    @Structure
    private ObjectFactory obf;

    @Service
    private LegalService legalService;

    @Service
    private PeopleRepository peopleRepository;

    @Structure
    private UnitOfWorkFactory uowf;
    private Identity baselId;
    private Identity montpellierId;
    private Identity hannoverId;
    private Identity malmoId;

    private Identity cherasId;
    private Identity unknown3Id;
    private Identity unknown2Id;
    private Identity unknown1Id;
    private Identity varnhemId;

    private Identity canaryId;
    private Identity switzerlandId;
    private Identity franceId;
    private Identity denmarkId;
    private Identity germanyId;
    private Identity swedenId;
    private Identity usId;
    private Identity malaysiaId;

    @Before
    public void setupTestData()
    {
        try( UnitOfWork uow = uowf.newUnitOfWork( UsecaseBuilder.newUsecase( "TestData Generation" ) ) )
        {
            testData();
            uow.complete();
        }
    }

    @Test
    public void validateAllCountriesPresent()
    {
        try( UnitOfWork uow = uowf.newUnitOfWork( UsecaseBuilder.newUsecase( "Test - validateAllCountriesPresent" ) ) )
        {
            assertThat( peopleRepository.findCountryByCountryCode( "my" ).name().get(), equalTo( "Malaysia" ) );
            assertThat( peopleRepository.findCountryByCountryCode( "us" ).name().get(), equalTo( "United States" ) );
            assertThat( peopleRepository.findCountryByCountryCode( "se" ).name().get(), equalTo( "Sweden" ) );
            assertThat( peopleRepository.findCountryByCountryCode( "de" ).name().get(), equalTo( "Germany" ) );
            assertThat( peopleRepository.findCountryByCountryCode( "dk" ).name().get(), equalTo( "Denmark" ) );
            assertThat( peopleRepository.findCountryByCountryCode( "fr" ).name().get(), equalTo( "France" ) );
            assertThat( peopleRepository.findCountryByCountryCode( "ch" ).name().get(), equalTo( "Switzerland" ) );
        }
    }

    @Test
    public void validateAllCitiesPresent()
    {
        try( UnitOfWork uow = uowf.newUnitOfWork( UsecaseBuilder.newUsecase( "Test - validateAllCitiesPresent" ) ) )
        {
            assertThat( peopleRepository.findCity( baselId ).name().get(), equalTo( "Basel" ) );
            assertThat( peopleRepository.findCity( malmoId ).name().get(), equalTo( "Malmo" ) );
            assertThat( peopleRepository.findCity( cherasId ).name().get(), equalTo( "Cheras" ) );
            assertThat( peopleRepository.findCity( hannoverId ).name().get(), equalTo( "Hannover" ) );
            assertThat( peopleRepository.findCity( montpellierId ).name().get(), equalTo( "Montpellier" ) );
        }
    }

    @Test
    public void validateAllAddressesPresent()
    {
        try( UnitOfWork uow = uowf.newUnitOfWork( UsecaseBuilder.newUsecase( "Test - validateAllAddressesPresent" ) ) )
        {
            Address canary = peopleRepository.findAddress( canaryId );
            assertThat( canary.street().get(), equalTo( "10, CH5A, Jalan Cheras Hartamas" ) );
            assertThat( canary.country().get().identity().get(), equalTo( malaysiaId ) );
            assertThat( canary.city().get().identity().get(), equalTo( cherasId ) );
            assertThat( canary.zipCode().get(), equalTo( "43200" ) );

            Address varnhem = peopleRepository.findAddress( varnhemId );
            assertThat( varnhem.street().get(), equalTo( "Varnhemsgatan 25" ) );
            assertThat( varnhem.city().get().identity().get(), equalTo( malmoId ) );
            assertThat( varnhem.country().get().identity().get(), equalTo( swedenId ) );
            assertThat( varnhem.zipCode().get(), equalTo( "215 00" ) );

            Address unknown = peopleRepository.findAddress( unknown1Id );
            assertThat( unknown.street().get(), equalTo( "" ) );
            assertThat( unknown.city().get().identity().get(), equalTo( montpellierId ) );
            assertThat( unknown.country().get().identity().get(), equalTo( franceId ) );
            assertThat( unknown.zipCode().get(), equalTo( "" ) );

            unknown = peopleRepository.findAddress( unknown2Id );
            assertThat( unknown.street().get(), equalTo( "" ) );
            assertThat( unknown.city().get().identity().get(), equalTo( hannoverId ) );
            assertThat( unknown.country().get().identity().get(), equalTo( germanyId ) );
            assertThat( unknown.zipCode().get(), equalTo( "" ) );

            unknown = peopleRepository.findAddress( unknown3Id );
            assertThat( unknown.street().get(), equalTo( "" ) );
            assertThat( unknown.city().get().identity().get(), equalTo( baselId ) );
            assertThat( unknown.country().get().identity().get(), equalTo( switzerlandId ) );
            assertThat( unknown.zipCode().get(), equalTo( "" ) );
        }
    }

    @Test
    public void validateAllPersonsPresent()
    {
        try( UnitOfWork uow = uowf.newUnitOfWork( UsecaseBuilder.newUsecase( "Test - validateAllPersonsPresent" ) ) )
        {
            Person niclas = peopleRepository.findPersonByName( "Niclas" );
            assertThat( niclas.name().get(), equalTo( "Niclas" ) );
            Person eric = peopleRepository.findPersonByName( "Eric" );
            assertThat( eric.name().get(), equalTo( "Eric" ) );
            Person paul = peopleRepository.findPersonByName( "Paul" );
            assertThat( paul.name().get(), equalTo( "Paul" ) );
            Person toni = peopleRepository.findPersonByName( "Toni" );
            assertThat( toni.name().get(), equalTo( "Toni" ) );
            Person janna = peopleRepository.findPersonByName( "Janna" );
            assertThat( janna.name().get(), equalTo( "Janna" ) );
            Person peter = peopleRepository.findPersonByName( "Peter" );
            assertThat( peter.name().get(), equalTo( "Peter" ) );
            Person oscar = peopleRepository.findPersonByName( "Oscar" );
            assertThat( oscar.name().get(), equalTo( "Oscar" ) );
            Person kalle = peopleRepository.findPersonByName( "Kalle" );
            assertThat( kalle.name().get(), equalTo( "Kalle" ) );
            Person andreas = peopleRepository.findPersonByName( "Andreas" );
            assertThat( andreas.name().get(), equalTo( "Andreas" ) );
        }
    }

    @Test
    public void givenTestDataWhenAddingNewNamedAssociationExpectAssociationAdded()
    {
        try( UnitOfWork uow = uowf.newUnitOfWork( UsecaseBuilder.newUsecase( "Test - givenTestDataWhenAddingNewNamedAssociationExpectAssociationAdded" ) ) )
        {
            Person niclas = peopleRepository.findPersonByName( "Niclas" );
            PhoneNumber newNumber = peopleRepository.createPhoneNumber( "+86-185-21320803" );
            niclas.phoneNumbers().put( "Mobile", newNumber );
            uow.complete();
        }
        try( UnitOfWork uow = uowf.newUnitOfWork( UsecaseBuilder.newUsecase( "Test - givenTestDataWhenAddingNewNamedAssociationExpectAssociationAdded" ) ) )
        {
            Person niclas = peopleRepository.findPersonByName( "Niclas" );
            NamedAssociation<PhoneNumber> numbers = niclas.phoneNumbers();
            assertThat( numbers.count(), equalTo( 2 ) );
            PhoneNumber mobile = numbers.get( "Mobile" );
            assertThat( mobile.countryCode().get(), equalTo( 86 ) );
            assertThat( mobile.areaCode().get(), equalTo( 185 ) );
            assertThat( mobile.number().get(), equalTo( "21320803" ) );
            PhoneNumber home = numbers.get( "Home" );
            assertThat( home.countryCode().get(), equalTo( 60 ) );
            assertThat( home.areaCode().get(), equalTo( 16 ) );
            assertThat( home.number().get(), equalTo( "7636344" ) );
        }
    }

    @Test
    public void whenIteratingNamedAssociationExpectIterationInOrder()
    {
        try( UnitOfWork uow = uowf.newUnitOfWork( UsecaseBuilder.newUsecase( "Test - whenIteratingNamedAssociationExpectIterationToSucceed" ) ) )
        {
            Person niclas = peopleRepository.findPersonByName( "Niclas" );
            PhoneNumber newNumber1 = peopleRepository.createPhoneNumber( "+86-185-21320803" );
            niclas.phoneNumbers().put( "Chinese", newNumber1 );
            PhoneNumber newNumber2 = peopleRepository.createPhoneNumber( "+46-70-9876543" );
            niclas.phoneNumbers().put( "Swedish", newNumber2 );
            PhoneNumber newNumber3 = peopleRepository.createPhoneNumber( "+49-444-2832989823" );
            niclas.phoneNumbers().put( "German", newNumber3 );
            uow.complete();
        }
        try( UnitOfWork uow = uowf.newUnitOfWork( UsecaseBuilder.newUsecase( "Test - whenIteratingNamedAssociationExpectIterationToSucceed" ) ) )
        {
            Person niclas = peopleRepository.findPersonByName( "Niclas" );
            assertThat( niclas.phoneNumbers(), containsInAnyOrder("Home", "Chinese", "Swedish", "German"));
        }
    }

    @Test
    public void givenTestDataWhenAddingSameNamedAssociationExpectAssociationModified()
    {
        try( UnitOfWork uow = uowf.newUnitOfWork( UsecaseBuilder.newUsecase( "Test - givenTestDataWhenAddingSameNamedAssociationExpectAssociationModified" ) ) )
        {
            Person niclas = peopleRepository.findPersonByName( "Niclas" );
            PhoneNumber newNumber = peopleRepository.createPhoneNumber( "+86-185-21320803" );
            niclas.phoneNumbers().put( "Home", newNumber );
            uow.complete();
        }
        try( UnitOfWork uow = uowf.newUnitOfWork( UsecaseBuilder.newUsecase( "Test - givenTestDataWhenAddingSameNamedAssociationExpectAssociationModified" ) ) )
        {
            Person niclas = peopleRepository.findPersonByName( "Niclas" );
            NamedAssociation<PhoneNumber> numbers = niclas.phoneNumbers();
            assertThat( numbers.count(), equalTo( 1 ) );
            PhoneNumber home = numbers.get( "Home" );
            assertThat( home.countryCode().get(), equalTo( 86 ) );
            assertThat( home.areaCode().get(), equalTo( 185 ) );
            assertThat( home.number().get(), equalTo( "21320803" ) );
        }
    }

    @Test
    public void whenNullingOptionalAssociationExpectSuccess()
    {
        try( UnitOfWork uow = uowf.newUnitOfWork( UsecaseBuilder.newUsecase( "Test - whenNullingOptionalAssociationExpectSuccess" ) ) )
        {
            Person toni = peopleRepository.findPersonByName( "Toni" );
            toni.spouse().set( null );
            uow.complete();
        }
    }

    @Test( expected = ConstraintViolationException.class )
    public void whenNullingNonOptionalAssociationExpectFailure()
    {
        try( UnitOfWork uow = uowf.newUnitOfWork( UsecaseBuilder.newUsecase( "Test - whenNullingOptionalAssociationExpectSuccess" ) ) )
        {
            Person toni = peopleRepository.findPersonByName( "Toni" );
            toni.nationality().set( null );
            uow.complete();
        }
    }

    @Test
    public void whenRemovingEntityExpectAggregatedEntitiesToBeRemoved()
    {
        Identity homePhoneId;
        Identity chinesePhoneId;
        Identity germanPhoneId;
        Identity swedishPhoneId;
        Identity switzerlandId;
        Identity malaysiaId;
        Identity canaryId;
        Identity despairStId;
        try( UnitOfWork uow = uowf.newUnitOfWork( UsecaseBuilder.newUsecase( "Test - whenRemovingEntityExpectAggregatedEntitiesToBeRemoved" ) ) )
        {
            Person niclas = peopleRepository.findPersonByName( "Niclas" );
            PhoneNumber newNumber1 = peopleRepository.createPhoneNumber( "+86-185-21320803" );
            niclas.phoneNumbers().put( "Chinese", newNumber1 );
            PhoneNumber newNumber2 = peopleRepository.createPhoneNumber( "+46-70-9876543" );
            niclas.phoneNumbers().put( "Swedish", newNumber2 );
            PhoneNumber newNumber3 = peopleRepository.createPhoneNumber( "+49-444-2832989823" );
            niclas.phoneNumbers().put( "German", newNumber3 );
            homePhoneId = niclas.phoneNumbers().get( "Home" ).identity().get();
            swedishPhoneId = niclas.phoneNumbers().get( "Swedish" ).identity().get();
            chinesePhoneId = niclas.phoneNumbers().get( "Chinese" ).identity().get();
            germanPhoneId = niclas.phoneNumbers().get( "German" ).identity().get();

            City basel = peopleRepository.findCity( baselId );
            Country switzerland = peopleRepository.findCountryByCountryCode( "ch" );
            niclas.movedToNewAddress( "DespairStreet 12A", "43HQ21", basel, switzerland, obf.newObject( Rent.Builder.class ).create( 1000, "EUR" ) );
            uow.complete();
        }
        try( UnitOfWork uow = uowf.newUnitOfWork( UsecaseBuilder.newUsecase( "Test - whenRemovingEntityExpectAggregatedEntitiesToBeRemoved" ) ) )
        {
            Person niclas = peopleRepository.findPersonByName( "Niclas" );
            assertThat( niclas.nationality().get().name().get(), equalTo( "Sweden" ) );
            assertThat( niclas.oldAddresses().count(), equalTo( 1 ) );
            assertThat( niclas.address().get().country().get().name().get(), equalTo( "Switzerland" ) );
            canaryId = niclas.oldAddresses().get( 0 ).identity().get();
            despairStId = niclas.address().get().identity().get();
            malaysiaId = niclas.oldAddresses().get( 0 ).country().get().identity().get();
            switzerlandId = niclas.address().get().country().get().identity().get();
        }
        try( UnitOfWork uow = uowf.newUnitOfWork( UsecaseBuilder.newUsecase( "Test - whenRemovingEntityExpectAggregatedEntitiesToBeRemoved" ) ) )
        {
            Person niclas = peopleRepository.findPersonByName( "Niclas" );
            uow.remove( niclas );
            uow.complete();
        }
        try( UnitOfWork uow = uowf.newUnitOfWork( UsecaseBuilder.newUsecase( "Test - whenRemovingEntityExpectAggregatedEntitiesToBeRemoved" ) ) )
        {
            Person niclas = peopleRepository.findPersonByName( "Niclas" );
        }
        catch( NoSuchEntityException e )
        {
            // expected
        }
        try( UnitOfWork uow = uowf.newUnitOfWork( UsecaseBuilder.newUsecase( "Test - whenRemovingEntityExpectAggregatedEntitiesToBeRemoved" ) ) )
        {
            peopleRepository.findPhoneNumberById( homePhoneId );
        }
        catch( NoSuchEntityException e )
        {
            // expected
        }
        try( UnitOfWork uow = uowf.newUnitOfWork( UsecaseBuilder.newUsecase( "Test - whenRemovingEntityExpectAggregatedEntitiesToBeRemoved" ) ) )
        {
            peopleRepository.findPhoneNumberById( chinesePhoneId );
        }
        catch( NoSuchEntityException e )
        {
            // expected
        }
        try( UnitOfWork uow = uowf.newUnitOfWork( UsecaseBuilder.newUsecase( "Test - whenRemovingEntityExpectAggregatedEntitiesToBeRemoved" ) ) )
        {
            peopleRepository.findPhoneNumberById( swedishPhoneId );
        }
        catch( NoSuchEntityException e )
        {
            // expected
        }
        try( UnitOfWork uow = uowf.newUnitOfWork( UsecaseBuilder.newUsecase( "Test - whenRemovingEntityExpectAggregatedEntitiesToBeRemoved" ) ) )
        {
            peopleRepository.findPhoneNumberById( germanPhoneId );
        }
        catch( NoSuchEntityException e )
        {
            // expected
        }
        try( UnitOfWork uow = uowf.newUnitOfWork( UsecaseBuilder.newUsecase( "Test - whenRemovingEntityExpectAggregatedEntitiesToBeRemoved" ) ) )
        {
            peopleRepository.findAddress( canaryId );
        }
        catch( NoSuchEntityException e )
        {
            // expected
        }
        try( UnitOfWork uow = uowf.newUnitOfWork( UsecaseBuilder.newUsecase( "Test - whenRemovingEntityExpectAggregatedEntitiesToBeRemoved" ) ) )
        {
            peopleRepository.findAddress( despairStId );
        }
        catch( NoSuchEntityException e )
        {
            // expected
        }
        try( UnitOfWork uow = uowf.newUnitOfWork( UsecaseBuilder.newUsecase( "Test - whenRemovingEntityExpectAggregatedEntitiesToBeRemoved" ) ) )
        {
            peopleRepository.findCountryByIdentity( switzerlandId );
            peopleRepository.findCountryByIdentity( malaysiaId );

            peopleRepository.findPersonByName( "Peter" );
            peopleRepository.findPersonByName( "Andreas" );
            peopleRepository.findPersonByName( "Toni" );
            peopleRepository.findPersonByName( "Paul" );
        }
    }

    @Test( expected = IllegalStateException.class )
    public void whenNoActiveUnitOfWorkExpectIllegalStateException()
    {
        peopleRepository.findCountryByIdentity( switzerlandId );
    }

    private void testData()
    {
        Country malaysia = peopleRepository.createCountry( "my", "Malaysia" );
        malaysiaId = malaysia.identity().get();
        Country us = peopleRepository.createCountry( "us", "United States" );
        usId = us.identity().get();
        Country sweden = peopleRepository.createCountry( "se", "Sweden" );
        swedenId = sweden.identity().get();
        Country germany = peopleRepository.createCountry( "de", "Germany" );
        germanyId = germany.identity().get();
        Country denmark = peopleRepository.createCountry( "dk", "Denmark" );
        denmarkId = denmark.identity().get();
        Country france = peopleRepository.createCountry( "fr", "France" );
        franceId = france.identity().get();
        Country switzerland = peopleRepository.createCountry( "ch", "Switzerland" );
        switzerlandId = switzerland.identity().get();
        City cheras = peopleRepository.createCity( "Cheras" );
        cherasId = cheras.identity().get();
        City malmo = peopleRepository.createCity( "Malmo" );
        malmoId = malmo.identity().get();
        City hannover = peopleRepository.createCity( "Hannover" );
        hannoverId = hannover.identity().get();
        City montpellier = peopleRepository.createCity( "Montpellier" );
        montpellierId = montpellier.identity().get();
        City basel = peopleRepository.createCity( "Basel" );
        baselId = basel.identity().get();
        Rent.Builder rentBuilder = obf.newObject( Rent.Builder.class );
        Rent rentCanary = rentBuilder.create( 3000, "MYR" );
        Rent rentVarnhem = rentBuilder.create( 9000, "SEK" );
        Rent rentUnknown1 = rentBuilder.create( 1200, "EUR" );
        Rent rentUnknown2 = rentBuilder.create( 900, "EUR" );
        Rent rentUnknown3 = rentBuilder.create( 2200, "EUR" );
        Address canaryResidence = peopleRepository.createAddress( "10, CH5A, Jalan Cheras Hartamas", "43200", cheras, malaysia, rentCanary );
        canaryId = canaryResidence.identity().get();
        Address varnhem = peopleRepository.createAddress( "Varnhemsgatan 25", "215 00", malmo, sweden, rentVarnhem );
        varnhemId = varnhem.identity().get();
        Address unknown1 = peopleRepository.createAddress( "", "", montpellier, france, rentUnknown1 );
        unknown1Id = unknown1.identity().get();
        Address unknown2 = peopleRepository.createAddress( "", "", hannover, germany, rentUnknown2 );
        unknown2Id = unknown2.identity().get();
        Address unknown3 = peopleRepository.createAddress( "", "", basel, switzerland, rentUnknown3 );
        unknown3Id = unknown3.identity().get();
        Person eric = peopleRepository.createPerson( "Eric", malaysia, canaryResidence, null, null );
        Person niclas = peopleRepository.createPerson( "Niclas", sweden, canaryResidence, null, peopleRepository.createPhoneNumber( "+60-16-7636344" ) );
        niclas.children().add( eric );
        Person kalle = peopleRepository.createPerson( "Kalle", sweden, varnhem, null, null );
        Person oscar = peopleRepository.createPerson( "Oscar", sweden, varnhem, null, null );
        Person peter = peopleRepository.createPerson( "Peter", germany, varnhem, null, peopleRepository.createPhoneNumber( "+46-70-1234567" ) );
        peter.children().add( kalle );
        peter.children().add( oscar );
        Person paul = peopleRepository.createPerson( "Paul", france, unknown1, null, peopleRepository.createPhoneNumber( "+33-88-333666999" ) );
        Person janna = peopleRepository.createPerson( "Janna", france, unknown2, null, peopleRepository.createPhoneNumber( "+49-11-22334455" ) );
        Person toni = peopleRepository.createPerson( "Toni", france, unknown2, janna, peopleRepository.createPhoneNumber( "+49-12-99887766" ) );
        janna.spouse().set( toni );
        Person andreas = peopleRepository.createPerson( "Andreas", germany, unknown3, null, peopleRepository.createPhoneNumber( "+41-98-1234567" ) );
        NamedAssociation<Person> niclasRels = niclas.relationships();
        niclasRels.put( FRIEND, peter );
        niclasRels.put( FRIEND, toni );
        niclasRels.put( FRIEND, andreas );
        niclasRels.put( FRIEND, paul );
        niclasRels.put( COLLEAGUE, toni );
        niclasRels.put( COLLEAGUE, andreas );
    }

    @Override
    protected void defineApplication( ApplicationAssembly applicationAssembly )
        throws AssemblyException
    {
        LayerAssembly accessLayer = applicationAssembly.layer( "Access Layer" );
        LayerAssembly domainLayer = applicationAssembly.layer( "Domain Layer" );
        LayerAssembly infrastructureLayer = applicationAssembly.layer( "Infrastructure Layer" );
        LayerAssembly configLayer = applicationAssembly.layer( "Configuration Layer" );
        accessLayer.uses( domainLayer.uses( infrastructureLayer.uses( configLayer ) ) );
        defineConfigModule( configLayer.module( "Configuration Module" ) );
        defineSerializationModule( configLayer.module( "Serialization Module" ) );
        defineStorageModule( infrastructureLayer.module( "Storage Module" ) );
        definePeopleModule( domainLayer.module( "People Module" ) );
        defineLegalModule( domainLayer.module( "Legal Module" ) );
        defineTestModule( accessLayer.module( "TestCase Module" ) );
    }

    @Override
    protected Application newApplicationInstance( ApplicationDescriptor applicationModel )
    {
        Application application = super.newApplicationInstance( applicationModel );
        Module module = application.findModule( "Access Layer", "TestCase Module" );
        module.injectTo( this );
        return application;
    }

    protected void defineTestModule( ModuleAssembly module )
    {
        module.defaultServices();
        module.objects( this.getClass() );
    }

    protected void definePeopleModule( ModuleAssembly module )
    {
        module.defaultServices();
        module.entities( Address.class, City.class, PhoneNumber.class );
        module.entities( Country.class, Person.class );
        module.services( PeopleRepository.class ).visibleIn( Visibility.application );
        module.values( Rent.class );
        module.objects( Rent.Builder.class ).visibleIn( Visibility.application );
    }

    protected void defineLegalModule( ModuleAssembly module )
    {
        module.defaultServices();
        module.services( LegalService.class ).visibleIn( Visibility.application );
        module.entities( Will.class, WillItem.class, WillPercentage.class, WillAmount.class );
    }

    protected void defineSerializationModule( ModuleAssembly module )
    {
        module.defaultServices();
        module.services( JsonSerialization.class ).visibleIn( Visibility.application );
    }

    protected abstract void defineStorageModule( ModuleAssembly module );

    protected void defineConfigModule( ModuleAssembly module )
    {
        module.defaultServices();
        module.services( MemoryEntityStoreService.class ).visibleIn( Visibility.module );
        configModule = module;
    }
}
