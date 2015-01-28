/*
* Name: Matt DeSilvey
*
*/
package bufmgr;

import chainexception.*;

public class PageAllocationException extends ChainException
{
	public PageAllocationException( Exception e, String name )
	{
		super( e, name );
	}
}