/*
 * Generated by ooc, the Object-Oriented C compiler, by Amos Wenger, 2009
 */

// OOC dependencies
#include "OocLib.h"
 Object GC_calloc(size_t nmemb, size_t size) {
	
	Object tmp = GC_malloc(nmemb  *  size);
	memset(tmp, 0, nmemb  *  size);
	return tmp; 
}