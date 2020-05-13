import React from "react";
import {BarrierPreview} from "../../components/search/BarrierPreview";
import {errorCodeToMessage} from "../../util/errorCode";
import {OptionalAlert} from "../../components/app/OptionalAlert";
import {SelectBox} from "../../components/general/SelectBox";
import {MapComponent} from "../../components/map/MapComponent";
import {Switch} from "../../components/general/Switch"
import "../../../styles/pages/login/search-results-page.css"
import {getBarrierUrl} from "../../util/RestEndpoints";

export class SearchResultsPage extends React.Component{

    constructor(props)
    {
        super(props);

        var urlParams = this.resolveURLParams();

        this.state = {results: [], loading: true, longitude: urlParams.longitude, latitude: urlParams.latitude,
                      radius: urlParams.radius, criterion: urlParams.sortCriterion, order: urlParams.sortOrder,
                      postCode: urlParams.postCode, filterOpen: false, error: null, map: urlParams.showMap};
    }

    componentDidMount()
    {
        this.getData(this.state.longitude, this.state.latitude, this.state.radius, this.state.postCode, this.state.map)
            .then(data => {
                this.setState({results: this.addDistance(this.sortDataByCriterion(data, this.state.criterion,
                                                                                  this.state.order),
                                                         this.state.longitude, this.state.latitude)});
            });
    }

    getSortCriteria(postCode)
    {
        var sortCriteria = [{label: "Titel", value:"title"},
                            {label: "Upvotes", value:"upVotes"},
                            {label: "Postleitzahl", value:"postcode"}];

        if (postCode == null) sortCriteria.push({label: "Distanz", value:"distance"});

        return(sortCriteria);
    }

    getSortOrders()
    {
        return([
            {label: "Aufsteigend", value: "asc"},
            {label: "Absteigend", value: "desc"}
        ]);
    }

    resolveURLParams = () =>
    {
        var urlParams = new URLSearchParams(window.location.search);

        var longitude = urlParams.get("lo");
        var latitude = urlParams.get("la");
        var radius = urlParams.get("ra");
        var postCode = urlParams.get("pc");
        var sortCriterion = urlParams.get("sc");
        var sortOrder = urlParams.get("so");
        var showMapString = urlParams.get("ma");

        var sortCriterionIsValid = false;
        this.getSortCriteria(postCode).forEach(criterion =>
            sortCriterionIsValid = criterion.value == sortCriterion ? true : sortCriterionIsValid);
        sortCriterion = sortCriterionIsValid ? sortCriterion :
            localStorage.getItem("lastSortCriterion") == null ? this.getSortCriteria(postCode)[0].value :
                localStorage.getItem("lastSortCriterion");

        sortCriterion = postCode != null && sortCriterion == "distance" ? this.getSortCriteria(postCode)[0].value
            : sortCriterion;

        localStorage.setItem("lastSortCriterion", sortCriterion);

        showMapString = ["true", "false"].includes(showMapString) ? showMapString :
            localStorage.getItem("lastShowMap") == null ? "false" : localStorage.getItem("lastShowMap");

        localStorage.setItem("lastShowMap", showMapString);
        var showMap = showMapString === "true";

        var sortOrderIsValid = false;
        this.getSortOrders().forEach(allowedSortOrder =>
            sortOrderIsValid = allowedSortOrder.value == sortOrder ? true : sortOrderIsValid);
        sortOrder = sortOrderIsValid ? sortOrder :
            localStorage.getItem("lastSortOrder") == null ? this.getSortOrders()[0].value :
                localStorage.getItem("lastSortOrder");

        localStorage.setItem("lastSortOrder", sortOrder);

        if (!this.validateParams(latitude, longitude, radius, postCode))
        {
            window.location.href = "/search";
            return;
        }

        longitude = parseFloat(longitude);
        latitude = parseFloat(latitude);
        radius = parseInt(radius);

        this.setURLParams(longitude, latitude, radius, postCode, sortCriterion, sortOrder, showMapString)

        return {longitude: longitude, latitude: latitude, radius: radius, sortCriterion: sortCriterion,
                sortOrder: sortOrder, postCode: postCode, showMap: showMap}
    }

    setURLParams(longitude, latitude, radius, postCode, sortCriterion, sortOrder, showMapString)
    {
        var urlParams = new URLSearchParams(window.location.search);

        if (!isNaN(longitude) && !isNaN(latitude) && !isNaN(radius) && postCode == null){
            urlParams.set("lo", longitude);
            urlParams.set("la", latitude);
            urlParams.set("ra", radius);
            urlParams.delete("pc");
        } else if (isNaN(longitude) && isNaN(latitude) && isNaN(radius) && postCode != null){
            urlParams.delete("lo");
            urlParams.delete("la");
            urlParams.delete("ra");
            urlParams.set("pc", postCode);
        } else {
            window.location.href = "/search";
            return;
        }

        urlParams.set("sc", sortCriterion);
        urlParams.set("so", sortOrder);
        urlParams.set("ma", showMapString)

        history.replaceState(history.state, document.title, "results?"+urlParams.toString());
    }

    validateParams(latitude, longitude, radius, postCode)
    {

        if (postCode == null){
            if (longitude == null || latitude == null || radius == null) return false;
            if (isNaN(longitude) || isNaN(latitude) || isNaN(radius)) return false;
            if (!(0 <= latitude && latitude <= 180 && 0 <= longitude && longitude <= 360)) return false;
        } else {
            if (longitude != null || latitude != null || radius != null) return false;
        }

        return true;
    }

    sortDataByCriterion = (data, criterion, order) =>
    {
        var sortedData = data.sort(function(first, second)
            {
                var firstValue = first[criterion];
                var secondValue = second[criterion];
                return ((firstValue < secondValue) ? -1 : ((firstValue > secondValue) ? 1 : 0));
            });
        return order == "desc" ? sortedData.reverse() : sortedData;
    }

    getData = (longitude, latitude, radius, postCode, map) => {
        var requestString;
        let hasErrorCode;
        let resultData;

        //earths circumference, 2x needed because search goes in every direction
        radius = map ? 40007863 : radius

        if (!isNaN(longitude))
        {
            requestString = "?longitude="+longitude+"&latitude="+latitude+"&radius="+radius;
        }
        else
        {
            requestString = "?postcode="+postCode;
        }

        return fetch(getBarrierUrl.concat(requestString), {
                method: 'GET',
                cache: 'no-cache',
                credentials: 'include'
            }).then(response => {
                hasErrorCode = !response.ok;
                return response.json();
            }).then(data => data)
            .catch(error => {
                console.error(error);
                this.setState({error: 'Ein unerwarteter Fehler ist aufgetreten'})
            });
    }

    addDistance(data, longitude, latitude)
    {
        var dataWithDistance = [];

        data.forEach(dataset => {
           dataset.distance = null;
           dataWithDistance.push(dataset);
        });

        return dataWithDistance;
    }

    onSortCriterionChange = (sortCriterion) =>
    {
       this.setState({criterion: sortCriterion,
                      results: this.sortDataByCriterion(this.state.results, sortCriterion, this.state.order)});
       this.setURLParams(this.state.longitude, this.state.latitude, this.state.radius, this.state.postCode,
                         sortCriterion, this.state.order, this.state.map);
       localStorage.setItem("lastSortCriterion", sortCriterion);
    }

    onSortOrderChange = (sortOrder) =>
    {
        this.setState({order: sortOrder,
                       results: this.sortDataByCriterion(this.state.results, this.state.criterion, sortOrder)});
        this.setURLParams(this.state.longitude, this.state.latitude, this.state.radius, this.state.postCode,
                          this.state.criterion, sortOrder, this.state.map);
        localStorage.setItem("lastSortOrder", sortOrder);
    }

    toggleMap = () => {
        var notThisStateMap = !this.state.map;

        this.getData(this.state.longitude, this.state.latitude, this.state.radius, this.state.postCode, notThisStateMap)
            .then(data => {
                this.setState({results: this.addDistance(this.sortDataByCriterion(data, this.state.criterion,
                                                                                  this.state.order),
                                                         this.state.longitude, this.state.latitude)});
            });

        this.setState({map: notThisStateMap});
        this.setURLParams(this.state.longitude, this.state.latitude, this.state.radius, this.state.postCode,
                          this.state.criterion, this.state.sortOrder, notThisStateMap);
        localStorage.setItem("lastShowMap", notThisStateMap);
    }

    clearError = () => {
        this.setState({error: null});
    }

    render()
    {
        return(
            <div className="results">
                <div className="results-header">
                    {!isNaN(this.state.latitude) &&
                        <p className="results-description-header">
                            <b>{Number(Math.round(this.state.longitude+'e2')+'e-2')}°</b> Länge,
                            <b> {Number(Math.round(this.state.latitude+'e2')+'e-2')}°</b> Breite
                            { !this.state.map &&
                               <span>{", "} <b> {this.state.radius}m</b> Umkreis:</span>
                            }
                        </p>
                    }
                    {this.state.postCode != null &&
                        <p className="results-description-header">
                            PLZ: <b>{this.state.postCode}</b>
                        </p>
                    }
                    <div>
                        {this.state.filterOpen &&
                            <div className="filter-menu">
                                <SelectBox label="Kriterium:" values={this.getSortCriteria(this.state.postCode)}
                                 onValueChange={this.onSortCriterionChange} defaultValue={this.state.criterion}/>
                                <SelectBox label="Reihenfolge:" values={this.getSortOrders()}
                                 onValueChange={this.onSortOrderChange} defaultValue={this.state.order}/>
                                <br className="map-break" />
                                <p style={{display: "inline-block", margin: "auto 5px auto 5px",
                                 transform: "translateY(3px)"}}>Karte:</p>
                                <Switch on={this.state.map} onClick={this.toggleMap}/>
                            </div>
                        }

                        <img className="filter-icon" onClick={() => this.state.filterOpen ?
                         this.setState({filterOpen : false}) : this.setState({filterOpen : true})}
                         src="images/icons/uicomponents/filter.png" />
                    </div>
                </div>

                <OptionalAlert display={this.state.error} error={this.state.error} onClose={this.clearError} />
                {!this.state.map &&
                    <div className="results-content">
                        {this.state.results.map(result => <BarrierPreview key={result._id} title={result.title}
                         icon={result.picturePath} description={result.description} distance={result.distance}
                         upvotes={result.upVotes} downvotes={result.downVotes} vote={result.vote}/>)}
                     </div>
                }
                {this.state.map &&
                     <MapComponent longitude={this.state.longitude} latitude={this.state.latitude}
                      results={this.state.results} />
                }
            </div>
        );
    }

}