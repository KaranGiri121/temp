part of 'pagination_bloc.dart';

@immutable
sealed class PaginationEvent {}

final class FetchData extends PaginationEvent {}
