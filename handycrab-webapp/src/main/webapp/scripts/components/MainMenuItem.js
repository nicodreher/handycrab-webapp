import React from "react";
 import "../../styles/components/main-menu-item.css"

 export class MainMenuItem extends React.Component{
     constructor(props){
         super(props);
     }

     render(){
         return(
             <div>
                 <div className="menuItem">
                     <a style={{display: "inline-block", width:"100%"}} href={this.props.url}>
                         <img style={{width: "25px", height: "auto", display: "inline-block", borderRadius: "50%",
                          marginRight: "5px"}} src={this.props.icon} alt={this.props.altText} />
                         {this.props.title}
                     </a>
                 </div>
                 <hr style={{margin: "0px", padding:"0px"}} />
             </div>
         );
     }
 }