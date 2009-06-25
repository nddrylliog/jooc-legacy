/*
 * Generated by ooc, the Object-Oriented C compiler, by Amos Wenger, 2009
 */

// OOC dependencies
#include "TreeView.h"


/**
 * A widget for displaying both trees and lists
 */

/*
 * Definition of class gtk.TreeView
 */

gtk_TreeView__class gtk_TreeView__classInstance;


GtkObject * __gtk_TreeView_getObject(struct gtk_TreeView*  this) {
	
	return GTK_OBJECT(this->view);


}

Void __gtk_TreeView_appendColumn_TreeViewColumn(struct gtk_TreeView*  this, struct gtk_TreeViewColumn*  column) {
	
	gtk_tree_view_append_column(GTK_TREE_VIEW(this->view), GTK_TREE_VIEW_COLUMN(column->class->__getObject(column)));


}

struct gtk_TreeView*  __gtk_TreeView_new() {

	gtk_TreeView this = GC_malloc(sizeof(struct gtk_TreeView));

	if(gtk_TreeView__classInstance == NULL) {
		gtk_TreeView__classInstance = GC_malloc(sizeof(struct gtk_TreeView__class));
		gtk_TreeView__classInstance ->name = "gtk.TreeView";
	}
	this->class = gtk_TreeView__classInstance;

	this->class->__getObject = (GtkObject * (*)(struct gtk_TreeView* )) &__gtk_TreeView_getObject;
	this->class->__connect_String_Func = (Void (*)(struct gtk_TreeView* , String, Func)) &__gtk_GObject_connect_String_Func;
	this->class->__connect_String_Func_GPointer = (Void (*)(struct gtk_TreeView* , String, Func, GPointer)) &__gtk_GObject_connect_String_Func_GPointer;
	this->class->__connectNaked_String_Func = (Void (*)(struct gtk_TreeView* , String, Func)) &__gtk_GObject_connectNaked_String_Func;
	this->class->__connectNaked_String_Func_GPointer = (Void (*)(struct gtk_TreeView* , String, Func, GPointer)) &__gtk_GObject_connectNaked_String_Func_GPointer;
	this->class->__emitByName_String = (Void (*)(struct gtk_TreeView* , String)) &__gtk_GObject_emitByName_String;
	this->class->__ref = (Void (*)(struct gtk_TreeView* )) &__gtk_GObject_ref;
	this->class->__unref = (Void (*)(struct gtk_TreeView* )) &__gtk_GObject_unref;
	this->class->__sink = (Void (*)(struct gtk_TreeView* )) &__gtk_GObject_sink;
	this->class->__setProperty_String_GValue__star = (Void (*)(struct gtk_TreeView* , String, GValue *)) &__gtk_GObject_setProperty_String_GValue__star;
	this->class->__getWidget = (GtkWidget * (*)(struct gtk_TreeView* )) &__gtk_Widget_getWidget;
	this->class->__setSensitive_Bool = (Void (*)(struct gtk_TreeView* , Bool)) &__gtk_Widget_setSensitive_Bool;
	this->class->__isRealized = (Bool (*)(struct gtk_TreeView* )) &__gtk_Widget_isRealized;
	this->class->__realize = (Void (*)(struct gtk_TreeView* )) &__gtk_Widget_realize;
	this->class->__forceRepaint_Bool = (Void (*)(struct gtk_TreeView* , Bool)) &__gtk_Widget_forceRepaint_Bool;
	this->class->__show = (Void (*)(struct gtk_TreeView* )) &__gtk_Widget_show;
	this->class->__showAll = (Void (*)(struct gtk_TreeView* )) &__gtk_Widget_showAll;
	this->class->__hide = (Void (*)(struct gtk_TreeView* )) &__gtk_Widget_hide;
	this->class->__destroy = (Void (*)(struct gtk_TreeView* )) &__gtk_Widget_destroy;
	this->class->__setPosition_gint_gint = (Void (*)(struct gtk_TreeView* , gint, gint)) &__gtk_Widget_setPosition_gint_gint;
	this->class->__setUSize_gint_gint = (Void (*)(struct gtk_TreeView* , gint, gint)) &__gtk_Widget_setUSize_gint_gint;
	this->class->__setEvents_Int = (Void (*)(struct gtk_TreeView* , Int)) &__gtk_Widget_setEvents_Int;
	this->class->__getAllocation = (GtkAllocation (*)(struct gtk_TreeView* )) &__gtk_Widget_getAllocation;
	this->class->__getWidth = (Int (*)(struct gtk_TreeView* )) &__gtk_Widget_getWidth;
	this->class->__getHeight = (Int (*)(struct gtk_TreeView* )) &__gtk_Widget_getHeight;
	this->class->__getStyle = (struct gtk_Style*  (*)(struct gtk_TreeView* )) &__gtk_Widget_getStyle;
	this->class->__appendColumn_TreeViewColumn = (Void (*)(struct gtk_TreeView* , struct gtk_TreeViewColumn* )) &__gtk_TreeView_appendColumn_TreeViewColumn;
	
	this->view = gtk_tree_view_new();

	return this;


}

struct gtk_TreeView*  __gtk_TreeView_new_TreeModel(struct gtk_TreeModel*  model) {

	gtk_TreeView this = GC_malloc(sizeof(struct gtk_TreeView));

	if(gtk_TreeView__classInstance == NULL) {
		gtk_TreeView__classInstance = GC_malloc(sizeof(struct gtk_TreeView__class));
		gtk_TreeView__classInstance ->name = "gtk.TreeView";
	}
	this->class = gtk_TreeView__classInstance;

	this->class->__getObject = (GtkObject * (*)(struct gtk_TreeView* )) &__gtk_TreeView_getObject;
	this->class->__connect_String_Func = (Void (*)(struct gtk_TreeView* , String, Func)) &__gtk_GObject_connect_String_Func;
	this->class->__connect_String_Func_GPointer = (Void (*)(struct gtk_TreeView* , String, Func, GPointer)) &__gtk_GObject_connect_String_Func_GPointer;
	this->class->__connectNaked_String_Func = (Void (*)(struct gtk_TreeView* , String, Func)) &__gtk_GObject_connectNaked_String_Func;
	this->class->__connectNaked_String_Func_GPointer = (Void (*)(struct gtk_TreeView* , String, Func, GPointer)) &__gtk_GObject_connectNaked_String_Func_GPointer;
	this->class->__emitByName_String = (Void (*)(struct gtk_TreeView* , String)) &__gtk_GObject_emitByName_String;
	this->class->__ref = (Void (*)(struct gtk_TreeView* )) &__gtk_GObject_ref;
	this->class->__unref = (Void (*)(struct gtk_TreeView* )) &__gtk_GObject_unref;
	this->class->__sink = (Void (*)(struct gtk_TreeView* )) &__gtk_GObject_sink;
	this->class->__setProperty_String_GValue__star = (Void (*)(struct gtk_TreeView* , String, GValue *)) &__gtk_GObject_setProperty_String_GValue__star;
	this->class->__getWidget = (GtkWidget * (*)(struct gtk_TreeView* )) &__gtk_Widget_getWidget;
	this->class->__setSensitive_Bool = (Void (*)(struct gtk_TreeView* , Bool)) &__gtk_Widget_setSensitive_Bool;
	this->class->__isRealized = (Bool (*)(struct gtk_TreeView* )) &__gtk_Widget_isRealized;
	this->class->__realize = (Void (*)(struct gtk_TreeView* )) &__gtk_Widget_realize;
	this->class->__forceRepaint_Bool = (Void (*)(struct gtk_TreeView* , Bool)) &__gtk_Widget_forceRepaint_Bool;
	this->class->__show = (Void (*)(struct gtk_TreeView* )) &__gtk_Widget_show;
	this->class->__showAll = (Void (*)(struct gtk_TreeView* )) &__gtk_Widget_showAll;
	this->class->__hide = (Void (*)(struct gtk_TreeView* )) &__gtk_Widget_hide;
	this->class->__destroy = (Void (*)(struct gtk_TreeView* )) &__gtk_Widget_destroy;
	this->class->__setPosition_gint_gint = (Void (*)(struct gtk_TreeView* , gint, gint)) &__gtk_Widget_setPosition_gint_gint;
	this->class->__setUSize_gint_gint = (Void (*)(struct gtk_TreeView* , gint, gint)) &__gtk_Widget_setUSize_gint_gint;
	this->class->__setEvents_Int = (Void (*)(struct gtk_TreeView* , Int)) &__gtk_Widget_setEvents_Int;
	this->class->__getAllocation = (GtkAllocation (*)(struct gtk_TreeView* )) &__gtk_Widget_getAllocation;
	this->class->__getWidth = (Int (*)(struct gtk_TreeView* )) &__gtk_Widget_getWidth;
	this->class->__getHeight = (Int (*)(struct gtk_TreeView* )) &__gtk_Widget_getHeight;
	this->class->__getStyle = (struct gtk_Style*  (*)(struct gtk_TreeView* )) &__gtk_Widget_getStyle;
	this->class->__appendColumn_TreeViewColumn = (Void (*)(struct gtk_TreeView* , struct gtk_TreeViewColumn* )) &__gtk_TreeView_appendColumn_TreeViewColumn;
	
	this->view = gtk_tree_view_new_with_model(model->class->__getModel(model));
	this->model = model;

	return this;


}