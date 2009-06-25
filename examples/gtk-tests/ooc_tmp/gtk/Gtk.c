/*
 * Generated by ooc, the Object-Oriented C compiler, by Amos Wenger, 2009
 */

// OOC dependencies
#include "Gtk.h"


/**
 * Singleton GTK class for initialization etc.
 */

/*
 * Definition of class gtk.Gtk
 */

gtk_Gtk__class gtk_Gtk__classInstance;


/**
 * Initialize Gtk, usually called from main, with Gtk.init(&argc, &argv)
 * @param argc a pointer to the number of arguments passed to the program
 * @param argv a pointer to the array of arguments as strings passed to the program
 */
Void __gtk_Gtk_init_Int__star_String__star__star(Int *argc, String **argv) {
	
	gtk_init(argc, argv);
}

/**
 * Start the Gtk main loop
 */
Void __gtk_Gtk_main() {
	
	gtk_main();
}

/**
 * @return true if the event queue is not empty
 */
Bool __gtk_Gtk_eventsPending() {
	
	return gtk_events_pending();
}

/**
 * Iterate the gtk main loop
 */
Void __gtk_Gtk_mainIteration() {
	
	gtk_main_iteration();
}

/**
 * Quit the Gtk main loop
 */
Void __gtk_Gtk_mainQuit() {
	
	gtk_main_quit();
}

/**
 * Add an object to the list of objects to be destroyed at the end
 * of the application
 * @param object
 */
Void __gtk_Gtk_quitAddDestroy_GObject(struct gtk_GObject*  object) {
	
	gtk_quit_add_destroy(1, GTK_OBJECT(object->class->__getObject(object)));
}

struct gtk_Gtk*  __gtk_Gtk_new() {

	gtk_Gtk this = GC_malloc(sizeof(struct gtk_Gtk));

	if(gtk_Gtk__classInstance == NULL) {
		gtk_Gtk__classInstance = GC_malloc(sizeof(struct gtk_Gtk__class));
		gtk_Gtk__classInstance ->name = "gtk.Gtk";
	}
	this->class = gtk_Gtk__classInstance;

	
	
	
	
	
	
	

	return this;


}
