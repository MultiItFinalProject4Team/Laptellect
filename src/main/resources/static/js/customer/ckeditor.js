import {
	ClassicEditor,
	AccessibilityHelp,
	Alignment,
	AutoImage,
	Autosave,
	Bold,
	CloudServices,
	Essentials,
	FindAndReplace,
	FontBackgroundColor,
	FontColor,
	FontFamily,
	FontSize,
	GeneralHtmlSupport,
	Highlight,
	HorizontalLine,
	ImageBlock,
	ImageInsert,
	ImageInsertViaUrl,
	ImageToolbar,
	ImageUpload,
	Italic,
	Link,
	Mention,
	Paragraph,
	RemoveFormat,
	SelectAll,
	SimpleUploadAdapter,
	SpecialCharacters,
	SpecialCharactersArrows,
	SpecialCharactersCurrency,
	SpecialCharactersEssentials,
	SpecialCharactersLatin,
	SpecialCharactersMathematical,
	SpecialCharactersText,
	Strikethrough,
	Underline,
	Undo
} from 'ckeditor5';

import translations from 'ckeditor5/translations/ko.js';

const editorConfig = {
	toolbar: {
		items: [
			'undo',
			'redo',
			'|',
			'findAndReplace',
			'selectAll',
			'|',
			'fontSize',
			'fontFamily',
			'fontColor',
			'fontBackgroundColor',
			'|',
			'bold',
			'italic',
			'underline',
			'strikethrough',
			'removeFormat',
			'|',
			'specialCharacters',
			'horizontalLine',
			'link',
			'insertImage',
			'highlight',
			'|',
			'alignment',
			'|',
			'accessibilityHelp'
		],
		shouldNotGroupWhenFull: false
	},
	plugins: [
		AccessibilityHelp,
		Alignment,
		AutoImage,
		Autosave,
		Bold,
		CloudServices,
		Essentials,
		FindAndReplace,
		FontBackgroundColor,
		FontColor,
		FontFamily,
		FontSize,
		GeneralHtmlSupport,
		Highlight,
		HorizontalLine,
		ImageBlock,
		ImageInsert,
		ImageInsertViaUrl,
		ImageToolbar,
		ImageUpload,
		Italic,
		Link,
		Mention,
		Paragraph,
		RemoveFormat,
		SelectAll,
		SimpleUploadAdapter,
		SpecialCharacters,
		SpecialCharactersArrows,
		SpecialCharactersCurrency,
		SpecialCharactersEssentials,
		SpecialCharactersLatin,
		SpecialCharactersMathematical,
		SpecialCharactersText,
		Strikethrough,
		Underline,
		Undo
	],
	fontFamily: {
		supportAllValues: true
	},
	fontSize: {
		options: [10, 12, 14, 'default', 18, 20, 22],
		supportAllValues: true
	},
	htmlSupport: {
		allow: [
			{
				name: /^.*$/,
				styles: true,
				attributes: true,
				classes: true
			}
		]
	},
	image: {
		toolbar: ['imageTextAlternative']
	},
	language: 'ko',
	link: {
		addTargetToExternalLinks: true,
		defaultProtocol: 'http://localhost:8099/customer/user/imageUpload',
		decorators: {
			toggleDownloadable: {
				mode: 'manual',
				label: 'Downloadable',
				attributes: {
					download: 'file'
				}
			}
		}
	},
	mention: {
		feeds: [
			{
				marker: '@',
				feed: [
					/* See: https://ckeditor.com/docs/ckeditor5/latest/features/mentions.html */
				]
			}
		]
	},
	placeholder: 'Type or paste your content here!',
    translations: [translations],
    simpleUpload: {
        uploadUrl: 'http://localhost:8099/customer/user/imageUpload' // 업로드 URL
    }
};

let editor;

ClassicEditor
.create( document.querySelector( '#content' ) , editorConfig)
.then(newEditor => {
    editor = newEditor;
});

document.addEventListener("DOMContentLoaded", function() {
    // 폼 제출 시 검증 로직
    document.querySelector('form').addEventListener('submit', function(event) {
        event.preventDefault(); // 폼 기본 제출 동작 방지

        // CKEditor의 내용 가져오기
        const content = editor.getData();

        // CKEditor 내용이 비어 있는지 확인
        if (!content.trim()) {
            swal("내용을 입력하세요", "", "error");
            return; // 폼 제출 중지
        }

        // 폼 제출 허용 (AJAX로 제출하는 경우에만 필요)
        this.submit();
    });
});