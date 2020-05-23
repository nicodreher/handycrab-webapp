import React from "react";
import {Col, Form, Image, Row, Spinner} from "react-bootstrap";
import {FormField} from "../general/FormField";
import Button from "react-bootstrap/Button";
import {OptionalAlert} from "../app/OptionalAlert";
import {addBarrierUrl, modifyBarrierUrl} from "../../util/RestEndpoints";
import {errorCodeToMessage} from "../../util/errorCode";
import Alert from "react-bootstrap/Alert";

export class AddForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            inAddMode: this.props.barrier === undefined,
            title: '',
            longitude: 0.0,
            latitude: 0.0,
            description: '',
            postcode: '',
            solution: '',
            fileName: 'Keine Datei ausgewählt',
            error: '',
            processing: 0
        };
        this.fileInput = React.createRef();
    }

    componentDidMount() {
        if (!(this.state.inAddMode)) {
            this.setState({...this.props.barrier});
        } else {
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(
                    (position) => {
                        this.setState({
                            latitude: position.coords.latitude.toFixed(6),
                            longitude: position.coords.longitude.toFixed(6),
                        });
                    });
            }
        }
    }

    /**
     * @see https://stackoverflow.com/questions/36280818/how-to-convert-file-to-base64-in-javascript
     * @param {Blob} file The blob to convert
     */
    toBase64 = (file) => new Promise((resolve, reject) => {
        if (file === null || file === undefined) {
            return resolve(undefined);
        }
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => resolve(reader.result.replace(/^data:.*\/.*;base64,/, ''));
        reader.onerror = error => reject(error);
    });

    handleSubmit = (event) => {
        event.preventDefault();

        if (this.validateInputs()) {
            this.setState({processing: 1})
            if (this.state.inAddMode) {
                this.toBase64(this.fileInput.current.files[0]).then((text) => {
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
                                postcode: this.state.postcode,
                                solution: this.state.solution
                            })
                    });
                }).then((response) => {
                    return response.json();
                }).then((data) => {
                    if (data.errorCode) {
                        this.setState({error: errorCodeToMessage(data.errorCode)});
                    } else {
                        this.setState({processing: 2});
                        window.location.replace(window.location.origin + "/detail?id=" + data._id);
                    }
                }).catch((error) => {
                    console.error(error);
                    this.setState({processing: 0, error: 'Barriere konnte nicht hinzugefügt werden'});
                });
            } else {
                this.toBase64(this.fileInput.current.files[0]).then((text) => {
                    const body = {_id: this.props.barrier._id};
                    if (this.props.barrier.description !== this.state.description) {
                        body.description = this.state.description;
                    }
                    if (this.props.barrier.title !== this.state.title) {
                        body.title = this.state.title;
                    }
                    body.picture = text;
                    return fetch(modifyBarrierUrl, {
                        method: 'PUT',
                        cache: 'no-cache',
                        headers: new Headers({
                            'Content-Type': 'application/json'
                        }),
                        credentials: 'include',
                        body: JSON.stringify(body)
                    });
                }).then(response => response.json()).then(data => {
                    if (data.errorCode) {
                        this.setState({error: errorCodeToMessage(data.errorCode)});
                    } else {
                        this.setState({processing: 2});
                        window.location.replace(window.location.origin + "/detail?id=" + data._id);
                    }
                }).catch(error => {
                    console.error(error);
                    this.setState({error: 'Die Änderungen an der Barriere konnten nicht gespeichert werden.'})
                });
            }
        }
    }

    /**
     * Validates the fields in the form that can be validated.
     * Ultimately this results in testing the file for size and MIME type and ensuring that the title string
     * does not only contain whitespace.
     * @return {boolean}
     */
    validateInputs = () => {

        const result = this.validateFile(this.fileInput.current?.files[0]);
        if (result.hasError) {
            this.setState({error: result.msg});
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
     * @return {{hasError: boolean, msg:string}} hasError is true if the validation failed. msg provides the reason for the failure
     */
    validateFile = (file) => {
        if (file === undefined || file === null) {
            return {hasError: false, msg: ''};
        }
        if (file.size > 8388608) {
            return {hasError: true, msg: 'Die hochgeladene Datei ist größer als 8 MB'};
        }
        if ((file.type === "image/png") || (file.type === "image/jpeg")) {
            return {hasError: false, msg: ''};
        } else {
            return {hasError: true, msg: 'Die hochgeladene Datei ist kein .jpg oder .png'};
        }
    }

    render() {
        const promisesRunning = this.state.processing === 1;
        const invalidTitle = /^\s*$/.test(this.state.title);
        const validFile = this.validateFile(this.fileInput.current?.files[0]);
        return <Form id='addForm' onSubmit={this.handleSubmit}>
            <OptionalAlert error={this.state.error} display={this.state.error}
                           onClose={() => this.setState({error: ''})}/>
            {(this.state.processing === 2) &&
            <Alert dismissible={true} onClose={() => this.setState({processing: 0})}
                   variant={'success'}>{this.state.inAddMode ? 'Barriere erfolgreich hinzugefügt' : 'Barriere erfolgreich geändert'} </Alert>}
            <div>&nbsp;</div>
            {!this.state.inAddMode && <>
                <div align={'center'}><Image src={this.state.picturePath} fluid/></div>
                <div align={'center'} text-align={'center'}>Aktuelles Bild</div>
                <br/>
            </>}

            <FormField id='barrier_title' label='Titel' required={true}
                       onChange={(event) => this.setState({title: event.target.value})} value={this.state.title}
                       type={'text'} isInvalid={invalidTitle}/>
            {this.state.inAddMode && <>
                <FormField id='barrier_latitude' label='Breitengrad' required={true}
                           onChange={(event) => this.setState({latitude: event.target.value})}
                           value={this.state.latitude} type='number' step='0.000001' min='-90' max='90'/>
                <FormField id='barrier_longitude' label='Längengrad' required={true}
                           onChange={(event) => this.setState({longitude: event.target.value})}
                           value={this.state.longitude} type='number' step='0.000001' min='-180' max='180'/>
                <FormField id='barrier_postcode' type='text' value={this.state.postcode}
                           onChange={(event) => this.setState({postcode: event.target.value})}
                           label='Postleitzahl' required={true}/></>}
            <FormField id='barrier_description' label='Beschreibung'
                       onChange={(event) => this.setState({description: event.target.value})}
                       value={this.state.description} as='textarea'/>
            {this.state.inAddMode && <FormField id='barrier_solution' label='Lösungsvorschlag'
                                                onChange={(event) => this.setState({solution: event.target.value})}
                                                value={this.state.solution} as='textarea'/>}
            <Form.Group as={Row}>
                <Form.Label id={'barrier_image_label'} htmlFor={'barrier_image'} column sm="2">
                    Bild der Barriere
                </Form.Label>
                <Col sm="10">
                    <Form.File id="barrier_image" label={this.state.fileName} custom ref={this.fileInput}
                               accept='.png, .jpg, .jpeg, image/png, image/jpeg' onChange={this.handleChangedFile}
                               isInvalid={!validFile} data-browse='Durchsuchen'/>
                </Col>
            </Form.Group>

            <Button type={"submit"} disabled={promisesRunning}>
                {this.state.inAddMode ? 'Barriere hinzufügen ' : 'Speichern '}
                {promisesRunning &&
                <Spinner as="span" animation="grow" size="sm" role="status" aria-hidden="true"/>}
            </Button>
            {!this.state.inAddMode && <>{' '}
                <Button variant={"danger"} onClick={this.props.switchMode}>Abbrechen</Button></>}
        </Form>
    }
}