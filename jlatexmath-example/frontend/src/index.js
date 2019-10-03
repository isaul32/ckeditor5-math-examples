import ClassicEditor from '@ckeditor/ckeditor5-build-classic';
import CKEditorInspector from '@ckeditor/ckeditor5-inspector';

ClassicEditor.create( document.querySelector( '#editor' ), {
    math: {
        engine: async ( equation, element, display ) => {
			try {
				if ( display ) {
					equation = '\\[' + equation + '\\]';
				} else {
					equation = '\\(' + equation + '\\)';
				}

				const url = '/api/render?tex=' + encodeURIComponent( equation );
				const res = await fetch( url ); // eslint-disable-line
				if ( !res.ok ) {
					throw new Error( 'Network response was not ok.' );
				}
                const blob = await res.blob();

                // Create img element
				const img = document.createElement( 'img' ); // eslint-disable-line
				img.src = URL.createObjectURL( blob ); // eslint-disable-line
				if ( display ) {
					img.setAttribute( 'style', 'display: block; margin: 0 auto;' );
				}

                // Update image
				if ( element.firstChild ) {
					element.firstChild.replaceWith( img );
				} else {
					element.appendChild( img );
				}
			} catch ( err ) {
				console.warn( 'There has been a problem with your fetch operation: ', err.message ); // eslint-disable-line
				element.textContent = 'Rendering error';
			}
		},
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
