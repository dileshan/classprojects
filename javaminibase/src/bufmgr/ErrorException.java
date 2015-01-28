/*
* Name: Matt DeSilvey
*
*/
package bufmgr;

import chainexception.*;

public class ErrorException extends ChainException {

	public ErrorException( Exception e, String name )
	{
		super( e, name );
	}
}
