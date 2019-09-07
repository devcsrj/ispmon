import React, {useEffect, useState} from 'react';
import SpeedtestChart from "./SpeedtestChart.jsx";
import Average from "./Average.jsx";
import ResultsRange from "./ResultsRange.jsx";
import {format, subDays} from "date-fns";
import 'skeleton-css/css/skeleton.css'

const App = () => {

  const [results, setResults] = useState([]);
  const fetchResults = async (since) => {
    const resp = await fetch("/results?since=" + since, {
      headers: {
        'Accept': 'application/json'
      }
    });
    resp.json().then(it => setResults(it));
  };
  const loadResults = (e) => fetchResults(e.target.value);

  useEffect(() => {
    fetchResults(format(subDays(new Date(), 1), 'yyyy-MM-dd'));
  }, []);

  return (
    <div className="container">
      <div className="row">
        <div className="four columns">
          <ResultsRange onChangeHandler={loadResults}/>
        </div>
        <div className="four columns">
          <Average results={results}/>
        </div>
        <div className="four columns">
        </div>
      </div>
      <div className="row">
        <div className="twelve columns">
          <SpeedtestChart results={results}/>
        </div>
      </div>
    </div>
  );
};

export default App;
