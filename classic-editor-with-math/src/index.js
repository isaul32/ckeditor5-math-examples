import ClassicEditor from '@ckeditor/ckeditor5-build-classic';
import CKEditorInspector from '@ckeditor/ckeditor5-inspector';

ClassicEditor.create( document.querySelector( '#editor' ), {
    math: {
        engine: 'katex',
        outputType: 'script',
        forceOutputType: false,
        enablePreview: true
    }
} )
    .then( editor => {
        CKEditorInspector.attach( editor );
        window.ckeditor = editor;
        getData();
        editor.model.document.on( 'change:data', () => {
            getData();
        } );
    } )
    .catch( err => {
        console.error( err );
    } );
function getData() {
    const data = window.ckeditor.getData();
    const preview = document.getElementById( 'editor-preview' );
    preview.innerHTML = data;
    document.getElementById( 'editor-preview-html' ).innerText = data;
}
