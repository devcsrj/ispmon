import React from "react";

const Average = ({results}) => {

  const len = results.length;
  const up = results
    .map((e) => e["upload"])
    .reduce((start, r) => start + r, 0) / len;
  const down = results
    .map((e) => e["download"])
    .reduce((start, r) => start + r, 0) / len;

  return (
    <div style={{padding: '8px'}}>
      <span style={{paddingRight: '10px'}}>
        ⬆️ {up ? up.toFixed(2) : 0} Mbps
      </span>
      <span style={{paddingLeft: '10px'}}>
        ⬇️ {down ? down.toFixed(2) : 0} Mbps
      </span>
    </div>
  );
};

export default Average;
