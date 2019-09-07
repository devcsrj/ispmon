import React from "react";
import {format, formatDistance, subDays} from "date-fns";

const ResultsRange = ({onChangeHandler}) => {
  const sinceOptions = (() => {
    const today = new Date();
    return [1, 7, 14, 30].map((e) => {
      const val = subDays(today, e);
      return <option
        key={"choice-" + e}
        value={format(val, 'yyyy-MM-dd')}>
        {formatDistance(val, today)}
      </option>
    }).reduce((arr, opt) => {
      arr.push(opt);
      return arr;
    }, []);
  })();

  return (
    <div>
      <span>Since: </span>
      <select onChange={onChangeHandler}>
        {sinceOptions}
      </select>
    </div>
  )

};

export default ResultsRange;
