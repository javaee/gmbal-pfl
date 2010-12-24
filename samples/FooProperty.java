interface FooProperty {
    @PropertyCreator
    interface FooPropertyMaker {
	FooProperty make( @Id("name") String name, @Id("value") int value ) ;

	@Default( @Id("value"), 0 ) 
	FooProperty make( @Id("name") ) ;
    }

    @Property String getName() ;

    @Property int getValue() ;
}
