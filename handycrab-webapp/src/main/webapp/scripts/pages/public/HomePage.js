import React from "react"

export class HomePage extends React.Component{

    constructor(props) {
        super(props);
        this.state = {time: new Date().toLocaleTimeString()};
    }

    componentDidMount() {
        this.intervalID = setInterval(() => this.setTime(), 1000);
    }

    componentWillUnmount() {
        clearInterval(this.intervalID);
    }

    setTime(){
        this.setState({time: new Date().toLocaleTimeString()});
    }

    render(){
        return(
            <div>
                <p id="demo-div">Hello World um {this.state.time} <br />This page is under construction!</p>
            </div>
        )
    }

}