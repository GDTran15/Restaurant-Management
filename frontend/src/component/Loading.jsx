import { forwardRef } from "react";

const Loading = forwardRef((props, ref) => {
  return <div ref={ref} className="loading"></div>;
});

export default Loading;