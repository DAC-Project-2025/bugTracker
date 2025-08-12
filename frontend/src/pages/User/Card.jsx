const Card = ({ title, provided, innerRef }) => {
  return (
    <div
      ref={innerRef}
      {...provided.draggableProps}
      {...provided.dragHandleProps}
      className="bg-white rounded shadow p-3 mb-2"
    >
      <p className="text-sm text-gray-800">{title}</p>
    </div>
  );
};

export default Card;
