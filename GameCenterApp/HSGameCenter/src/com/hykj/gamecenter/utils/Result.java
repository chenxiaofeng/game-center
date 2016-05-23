package com.hykj.gamecenter.utils;public class Result {    public String resultStatus;    String result;    String memo;    public Result( String rawResult ) {	try {	    String [] resultParams = rawResult.split( ";" );	    for( String resultParam : resultParams ) {		if( resultParam.startsWith( "resultStatus" ) ) {		    resultStatus = gatValue( resultParam , "resultStatus" );		}		if( resultParam.startsWith( "result" ) ) {		    result = gatValue( resultParam , "result" );		}		if( resultParam.startsWith( "memo" ) ) {		    memo = gatValue( resultParam , "memo" );		}	    }	}	catch ( Exception e ) {	    e.printStackTrace( );	}    }    @Override    public String toString() {	return "resultStatus={" + resultStatus + "};memo={" + memo + "};result={" + result + "}";    }    private String gatValue( String content , String key ) {	String prefix = key + "={";	return content.substring( content.indexOf( prefix ) + prefix.length( ) , content.lastIndexOf( "}" ) );    }}