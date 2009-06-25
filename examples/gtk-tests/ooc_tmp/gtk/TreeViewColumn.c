/*
 * Generated by ooc, the Object-Oriented C compiler, by Amos Wenger, 2009
 */

// OOC dependencies
#include "TreeViewColumn.h"

/*
 * Definition of class gtk.TreeViewColumn
 */

gtk_TreeViewColumn__class gtk_TreeViewColumn__classInstance;


GtkObject * __gtk_TreeViewColumn_getObject(struct gtk_TreeViewColumn*  this) {
	
	return GTK_OBJECT(this->column);


}

/**
 * Create a new TreeViewColumn to the first colun
 */
struct gtk_TreeViewColumn*  __gtk_TreeViewColumn_new_String(String titleColumn) {

	gtk_TreeViewColumn this = GC_malloc(sizeof(struct gtk_TreeViewColumn));

	if(gtk_TreeViewColumn__classInstance == NULL) {
		gtk_TreeViewColumn__classInstance = GC_malloc(sizeof(struct gtk_TreeViewColumn__class));
		gtk_TreeViewColumn__classInstance ->name = "gtk.TreeViewColumn";
	}
	this->class = gtk_TreeViewColumn__classInstance;

	this->class->__getObject = (GtkObject * (*)(struct gtk_TreeViewColumn* )) &__gtk_TreeViewColumn_getObject;
	this->class->__connect_String_Func = (Void (*)(struct gtk_TreeViewColumn* , String, Func)) &__gtk_GObject_connect_String_Func;
	this->class->__connect_String_Func_GPointer = (Void (*)(struct gtk_TreeViewColumn* , String, Func, GPointer)) &__gtk_GObject_connect_String_Func_GPointer;
	this->class->__connectNaked_String_Func = (Void (*)(struct gtk_TreeViewColumn* , String, Func)) &__gtk_GObject_connectNaked_String_Func;
	this->class->__connectNaked_String_Func_GPointer = (Void (*)(struct gtk_TreeViewColumn* , String, Func, GPointer)) &__gtk_GObject_connectNaked_String_Func_GPointer;
	this->class->__emitByName_String = (Void (*)(struct gtk_TreeViewColumn* , String)) &__gtk_GObject_emitByName_String;
	this->class->__ref = (Void (*)(struct gtk_TreeViewColumn* )) &__gtk_GObject_ref;
	this->class->__unref = (Void (*)(struct gtk_TreeViewColumn* )) &__gtk_GObject_unref;
	this->class->__sink = (Void (*)(struct gtk_TreeViewColumn* )) &__gtk_GObject_sink;
	this->class->__setProperty_String_GValue__star = (Void (*)(struct gtk_TreeViewColumn* , String, GValue *)) &__gtk_GObject_setProperty_String_GValue__star;
	
	String title = titleColumn;
	Int columnId = 0;
	GtkCellRenderer * renderer = gtk_cell_renderer_text_new();
	this->column = gtk_tree_view_column_new_with_attributes(title, renderer, NULL);
	gtk_tree_view_column_add_attribute(this->column, renderer, "text", columnId);
	gtk_tree_view_column_pack_start(this->column, renderer, true);;

	return this;


}

struct gtk_TreeViewColumn*  __gtk_TreeViewColumn_new_String_Int(String title, Int columnId) {

	gtk_TreeViewColumn this = GC_malloc(sizeof(struct gtk_TreeViewColumn));

	if(gtk_TreeViewColumn__classInstance == NULL) {
		gtk_TreeViewColumn__classInstance = GC_malloc(sizeof(struct gtk_TreeViewColumn__class));
		gtk_TreeViewColumn__classInstance ->name = "gtk.TreeViewColumn";
	}
	this->class = gtk_TreeViewColumn__classInstance;

	this->class->__getObject = (GtkObject * (*)(struct gtk_TreeViewColumn* )) &__gtk_TreeViewColumn_getObject;
	this->class->__connect_String_Func = (Void (*)(struct gtk_TreeViewColumn* , String, Func)) &__gtk_GObject_connect_String_Func;
	this->class->__connect_String_Func_GPointer = (Void (*)(struct gtk_TreeViewColumn* , String, Func, GPointer)) &__gtk_GObject_connect_String_Func_GPointer;
	this->class->__connectNaked_String_Func = (Void (*)(struct gtk_TreeViewColumn* , String, Func)) &__gtk_GObject_connectNaked_String_Func;
	this->class->__connectNaked_String_Func_GPointer = (Void (*)(struct gtk_TreeViewColumn* , String, Func, GPointer)) &__gtk_GObject_connectNaked_String_Func_GPointer;
	this->class->__emitByName_String = (Void (*)(struct gtk_TreeViewColumn* , String)) &__gtk_GObject_emitByName_String;
	this->class->__ref = (Void (*)(struct gtk_TreeViewColumn* )) &__gtk_GObject_ref;
	this->class->__unref = (Void (*)(struct gtk_TreeViewColumn* )) &__gtk_GObject_unref;
	this->class->__sink = (Void (*)(struct gtk_TreeViewColumn* )) &__gtk_GObject_sink;
	this->class->__setProperty_String_GValue__star = (Void (*)(struct gtk_TreeViewColumn* , String, GValue *)) &__gtk_GObject_setProperty_String_GValue__star;
	
	GtkCellRenderer * renderer = gtk_cell_renderer_text_new();
	this->column = gtk_tree_view_column_new_with_attributes(title, renderer, NULL);
	gtk_tree_view_column_add_attribute(this->column, renderer, "text", columnId);
	gtk_tree_view_column_pack_start(this->column, renderer, true);

	return this;


}
