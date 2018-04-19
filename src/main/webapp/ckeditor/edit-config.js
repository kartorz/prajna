/**
 * @license Copyright (c) 2003-2017, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function( config ) {
	// Define changes to default configuration here.
	// For complete reference see:
	// http://docs.ckeditor.com/#!/api/CKEDITOR.config

	// The toolbar groups arrangement, optimized for two toolbar rows.
	config.toolbarGroups = [
		{ name: 'clipboard',   groups: [ 'clipboard', 'undo' ] },
		{ name: 'editing',     groups: [ 'find', 'selection', 'spellchecker' ] },
		{ name: 'links' },
		{ name: 'insert' },
		{ name: 'forms' },
		{ name: 'tools' },
		{ name: 'document',	   groups: [ 'mode', 'document', 'doctools' ] },
		{ name: 'others' },
		'/',
		{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
		{ name: 'paragraph',   groups: [ 'list', 'indent', 'blocks', 'align', 'bidi' ] },
		{ name: 'styles' },
		{ name: 'colors' },
		{ name: 'align'  },
		{ name: 'about' }
	];

	config.extraPlugins = 'uploadimage,image2,colorbutton,colordialog,font,pastefromword,' + 
		'liststyle,justify,codesnippet,wordcount,notification';
	// Remove some buttons provided by the standard plugins, which are
	// not needed in the Standard(s) toolbar.
	config.removeButtons = 'Underline,Subscript,Superscript';

	// Set the most common block elements.
	config.format_tags = 'p;h1;h2;h3;pre';

	// Simplify the dialog windows.
	config.removeDialogTabs = 'image:advanced;link:advanced';

	//config.imageUploadUrl = '/uploader/upload?type=Images';

        //config.filebrowserUploadUrl = '/ckfinder/core/';
	//config.filebrowserImageUploadUrl = '/uploader/upload?type=Images';
	
	config.colorButton_colors = '1ABC9C,2ECC71,3498DB,9B59B6,4E5F70,F1C40F,' +
		'16A085,27AE60,2980B9,8E44AD,2C3E50,F39C12,' +
		'E67E22,E74C3C,ECF0F1,95A5A6,DDD,FFF,0000cc,' +
		'D35400,C0392B,BDC3C7,7F8C8D,999,000';
	
	config.font_defaultLabel = 'Arial';
	config.fontSize_defaultLabel = '16';
	config.bodyClass = 'document-editor';
	config.language = 'zh';
	
	config.wordcount = {
		showParagraphs: false,
		showWordCount: false,
		showCharCount: true,
		countSpacesAsChars: true,
		countHTML: true,
		maxCharCount: 1048500,
		filter: new CKEDITOR.htmlParser.filter({
			elements: {
				img: function( element ) {
					//console.log(element.attributes.src);
					if (element.attributes.src.indexOf('data:image/') === 0) {
						return false;
					}
		        }
		    }
		})
	};
};
