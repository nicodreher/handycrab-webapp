import React from "react";
import {BarrierPreview} from "../../components/search/BarrierPreview";
import {errorCodeToMessage} from "../../util/errorCode";
import {OptionalAlert} from "../../components/app/OptionalAlert";
import {SelectBox} from "../../components/general/SelectBox";
import "../../../styles/pages/login/search-results-page.css"
import {getBarrierUrl} from "../../util/RestEndpoints";

export class SearchResultsPage extends React.Component{

    constructor(props)
    {
        super(props);

        var urlParams = this.resolveURLParams();

        var restResults = this.generateTestData(urlParams.longitude, urlParams.latitude, urlParams.radius, urlParams.postCode);
        restResults = this.sortDataByCriterion(restResults, urlParams.sortCriterion,  urlParams.sortOrder);
        restResults = this.addDistance(restResults, urlParams.longitude, urlParams.latitude);

        this.state = {results: restResults, loading: true, longitude: urlParams.longitude, latitude: urlParams.latitude,
                      radius: urlParams.radius, criterion: urlParams.sortCriterion, order: urlParams.sortOrder,
                      postCode: urlParams.postCode, filterOpen: false, error: null};
    }

    getSortCriteria(postCode)
    {
        var sortCriteria = [{label: "Titel", value:"title"},
                            {label: "Upvotes", value:"upvotes"},
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

        var sortCriterionIsValid = false;
        this.getSortCriteria(postCode).forEach(criterion =>
            sortCriterionIsValid = criterion.value == sortCriterion ? true : sortCriterionIsValid);
        sortCriterion = sortCriterionIsValid ? sortCriterion :
            localStorage.getItem("lastSortCriterion") == null ? this.getSortCriteria(postCode)[0].value :
                localStorage.getItem("lastSortCriterion");

        sortCriterion = postCode != null && sortCriterion == "distance" ? this.getSortCriteria(postCode)[0].value
            : sortCriterion;

        localStorage.setItem("lastSortCriterion", sortCriterion);

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

        this.setURLParams(longitude, latitude, radius, postCode, sortCriterion, sortOrder)

        return {longitude: longitude, latitude: latitude, radius: radius, sortCriterion: sortCriterion,
                sortOrder: sortOrder, postCode: postCode}
    }

    setURLParams(longitude, latitude, radius, postCode, sortCriterion, sortOrder)
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

    generateTestData(amount, longitude, latitude, radius)
    {
        var testData = []

        const imageUrls = [
            "https://cdn.pixabay.com/photo/2016/11/08/04/49/jungle-1807476_1280.jpg",
            "https://cdn.pixabay.com/photo/2013/11/28/10/36/painting-220060_1280.jpg",
            "https://cdn.pixabay.com/photo/2013/11/27/14/38/prison-fence-219264_1280.jpg"
        ]
        const loremIpsum = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";
        const votePossibilities = ["NONE", "NONE", "NONE", "NONE", "UP", "DOWN"]

        var i;
        for (i = 0; i < amount; i++)
        {
            var randomLongitudeDistance = Math.random() * (radius - 1);
            var resultingLatitudeDistance = Math.random() * (radius - randomLongitudeDistance - 1);
            var voteDecision = votePossibilities[Math.floor(Math.random() * votePossibilities.length)];

            testData.push({
                _id: i,
                userId: 0,
                title: "Barriere "+ i,
                longitude: randomLongitudeDistance + longitude,
                latitude: resultingLatitudeDistance + latitude,
                picture: imageUrls[Math.floor(Math.random() * imageUrls.length)],
                description: loremIpsum,
                postcode: "00000",
                solution: [{
                            _id: i,
                            text: loremIpsum,
                            userId: 0,
                            upvotes: Math.floor(Math.random() * 10),
                            downvotes: Math.floor(Math.random() * 10),
                            vote: votePossibilities[Math.floor(Math.random() * votePossibilities.length)]
                          }],
                upvotes: Math.floor(Math.random() * 10) + (voteDecision == "UP" ? 1 : 0),
                downvotes: Math.floor(Math.random() * 10) + (voteDecision == "DOWN" ? 1 : 0),
                vote: voteDecision
            });
        }

        return testData;
    }

    addDistance(data, longitude, latitude)
    {
        var dataWithDistance = [];

        data.forEach(dataset => {
           dataset.distance = Math.round(Math.sqrt(Math.pow((dataset.longitude - longitude), 2)
                                         + Math.pow((dataset.latitude - latitude), 2)));
           dataWithDistance.push(dataset);
        });

        return dataWithDistance;
    }

    onSortCriterionChange = (sortCriterion) =>
    {
       this.setState({criterion: sortCriterion,
                      results: this.sortDataByCriterion(this.state.results, sortCriterion, this.state.order)});
       this.setURLParams(this.state.longitude, this.state.latitude, this.state.radius, this.state.postCode,
                         sortCriterion, this.state.order);
       localStorage.setItem("lastSortCriterion", sortCriterion);
    }

    onSortOrderChange = (sortOrder) =>
    {
        this.setState({order: sortOrder,
                       results: this.sortDataByCriterion(this.state.results, this.state.criterion, sortOrder)});
        this.setURLParams(this.state.longitude, this.state.latitude, this.state.radius, this.state.postCode,
                          this.state.criterion, sortOrder);
        localStorage.setItem("lastSortOrder", sortOrder);
    }

    render()
    {
        return(
            <div className="results">
                <div className="results-header">
                    {!isNaN(this.state.latitude) &&
                        <p className="results-description-header">
                            <b>{this.state.longitude}°</b> Länge, <b>{this.state.latitude}° </b>Breite,
                            {" "}<b>{this.state.radius}m</b> Umkreis:
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
                            </div>
                        }

                        <img className="filter-icon" onClick={() => this.state.filterOpen ?
                         this.setState({filterOpen : false}) : this.setState({filterOpen : true})}
                         src="images/icons/uicomponents/filter.png" />
                    </div>
                </div>
                <div className="results-content">
                    <OptionalAlert display={this.state.error} error={this.state.error} onClose={this.clearError} />
                    {this.state.results.map(result => <BarrierPreview key={result._id} title={result.title}
                     icon={result.picture} description={result.description} distance={result.distance}
                     upvotes={result.upvotes} downvotes={result.downvotes} vote={result.vote}/>)}
                 </div>
            </div>
        );
    }

}