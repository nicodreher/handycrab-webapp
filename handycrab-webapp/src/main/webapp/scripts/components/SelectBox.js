import React from "react"
import "../../styles/components/select-box.css"

export class SelectBox extends React.Component{

    constructor(props){
        super(props);
        this.state = {value: this.props.values.length > 0 ? this.props.values[0].value : ""};
    }

    handleChange = (event) => {
        if (this.props.onValueChange != null){
            this.props.onValueChange(event.target.value);
        }
    }

    render(){
        return(
            <label className="select-box">
                <p>{this.props.label}</p>
                <select onChange={this.handleChange} defaultValue={this.props.defaultValue}>
                    {this.props.values.map(currentValue => {
                        return(
                            <option key={currentValue.value} value={currentValue.value}>
                                {currentValue.label}
                            </option>
                        );
                    })}
                </select>
            </label>
        );
    }
}