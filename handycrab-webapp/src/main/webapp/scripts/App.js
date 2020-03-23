import React from "react"
import ReactDOM from "react-dom"

class App extends React.Component{

    constructor(props) {
        super(props);
        this.state = {time: new Date().toLocaleTimeString()};
    }

    componentDidMount() {
        this.intervalID = setInterval(
            () => this.setTime(),
            1000
        );
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
                <p id="demo-div">Hello World um {this.state.time}</p>
            </div>
        )
    }
}

ReactDOM.render(<App />, document.getElementById("app"));