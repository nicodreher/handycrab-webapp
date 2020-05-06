import React from "react";
import {Col, Form, Row} from "react-bootstrap";
import {FormField} from "../general/FormField";
import Button from "react-bootstrap/Button";
import {OptionalAlert} from "../app/OptionalAlert";
import {addBarrierUrl} from "../../util/RestEndpoints";
import {errorCodeToMessage} from "../../util/errorCode";

export class AddForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            title: '',
            longitude: 0.0,
            latitude: 0.0,
            description: '',
            postal: '01001',
            solution: '',
            fileName: '',
            error: ''
        };
        this.fileInput = React.createRef();
    }

    /**
     * @see https://stackoverflow.com/questions/36280818/how-to-convert-file-to-base64-in-javascript
     * @param {Blob} file The blob to convert
     */
    toBase64 = (file) => new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => resolve(reader.result.replace(/^data:.*\/.*;base64,/, ''));
        reader.onerror = error => reject(error);
    });

    handleSubmit = (event) => {
        event.preventDefault();

        if (this.validateInputs()) {
            this.toBase64(this.fileInput.current.files[0]).then((text) => {
                let postcode = this.state.postal;
                postcode = postcode.length === 4 ? '0' + postcode : postcode;
                return fetch(addBarrierUrl, {
                    method: 'POST',
                    cache: 'no-cache',
                    headers: new Headers({
                        'Content-Type': 'application/json'
                    }),
                    credentials: 'include',
                    body: JSON.stringify(
                        {
                            title: this.state.title,
                            longitude: parseFloat(this.state.longitude),
                            latitude: parseFloat(this.state.latitude),
                            picture: text,
                            description: this.state.description,
                            postcode: postcode,
                            solution: this.state.solution
                        })
                });
            }).then((response) => {
                return response.json();
            }).then((data) => {
                if (data.errorCode) {
                    this.setState({error: errorCodeToMessage(data.errorCode)});
                } else {
                    //TODO success case, prolly redirect to detailansicht
                }

            })
        }
    }

    /**
     *
     * @return {boolean}
     */
    validateInputs = () => {

        if (!this.validateFile(this.fileInput.current?.files[0])) {
            this.setState({error: 'Die hochgeladene Datei ist kein .jpg oder .png'});
            return false;
        }
        if (/^\s*$/.test(this.state.title)) {
            this.setState({error: 'Der Titel der Barriere enthält keine Buchstaben'});
            return false;
        }
        return true;
    }
    handleChangedFile = (event) => {
        this.setState({fileName: this.fileInput.current.files[0].name})
    }
    /**
     * Checks to see if a file is null or undefined. If not it checks if the MIME type is valid.
     * The only valid MIME types are image/jpeg and image/png.
     *
     * @param file {File} The file to check
     * @return {boolean} True if the file does not exist or has the appropriate MIME type
     */
    validateFile = (file) => {
        if (file === undefined || file === null) {
            return true;
        }
        if (file.size > 8388608) {
            this.setState({error: "Die hochgeladene Datei ist zu groß"});
            return false;
        }
        return (file.type === "image/png") || (file.type === "image/jpeg");
    }

    render() {
        const invalidTitle = /^\s*$/.test(this.state.title);
        const validFile = this.validateFile(this.fileInput.current?.files[0]);
        return <Form id='addForm' onSubmit={this.handleSubmit}>
            <OptionalAlert error={this.state.error} display={this.state.error}
                           onClose={() => this.setState({error: ''})}/>
            <div>&nbsp;</div>
            <FormField id='barrier_title' label='Titel' required={true}
                       onChange={(event) => this.setState({title: event.target.value})} value={this.state.title}
                       type={'text'} isInvalid={invalidTitle}/>
            <FormField id='barrier_latitude' label='Breitengrad' required={true}
                       onChange={(event) => this.setState({latitude: event.target.value})} value={this.state.latitude}
                       type='number' step='0.000001' min='-90' max='90'/>
            <FormField id='barrier_longitude' label='Längengrad' required={true}
                       onChange={(event) => this.setState({longitude: event.target.value})} value={this.state.longitude}
                       type='number' step='0.000001' min='-180' max='180'/>
            <FormField id='barrier_postcode' type='number' value={this.state.postal}
                       onChange={(event) => this.setState({postal: event.target.value})} min='01001' max='99999'
                       label='Postleitzahl' required={true}/>
            <FormField id='barrier_description' label='Beschreibung'
                       onChange={(event) => this.setState({description: event.target.value})}
                       value={this.state.description} type={'text'}/>
            <FormField id='barrier_solution' label='Lösungsvorschlag'
                       onChange={(event) => this.setState({solution: event.target.value})} value={this.state.solution}
                       type={'text'}/>
            <Form.Group as={Row}>
                <Form.Label id={'barrier_image_label'} htmlFor={'barrier_image'} column sm="2">
                    Bild der Barriere
                </Form.Label>
                <Col sm="10">
                    <Form.File id="barrier_image" label={this.state.fileName} custom ref={this.fileInput}
                               accept='.png, .jpg, .jpeg, image/png, image/jpeg' onChange={this.handleChangedFile}
                               isInvalid={!validFile}/>
                </Col>
            </Form.Group>

            <Button type={"submit"}>
                Barriere hinzufügen
            </Button>
        </Form>
    }
}